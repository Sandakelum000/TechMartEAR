package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.OrderDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.*;
import lk.techmart.core.event.InventoryUpdateEvent;
import lk.techmart.core.mapper.OrderMapper;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.core.util.Validator;
import lk.techmart.ejb.event.Logger;
import lk.techmart.ejb.jms.producer.InventoryTopicPublisher;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class OrderServiceBean implements OrderService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Inject
    private InventoryTopicPublisher inventoryTopicPublisher;

    @Override
    public OrderDTO createPendingOrder(UserSessionDTO sessionUser) {

        User user = entityManager.find(User.class, sessionUser.getId());
        if (user == null) return null;

        List<Cart> cartList = entityManager.createQuery("FROM Cart c WHERE c.user=:user", Cart.class)
                .setParameter("user", user)
                .getResultList();

        if (cartList.isEmpty()) return null;

        Status pendingStatus = entityManager.createNamedQuery("Status.findByValue", Status.class)
                .setParameter("value", String.valueOf(Status.Type.PENDING))
                .getSingleResult();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(pendingStatus);
        order.setStartTime(System.currentTimeMillis());

        List<OrderItem> orderItemList = new ArrayList<>();

        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQty(cart.getQty());
            orderItem.setStock(cart.getStock());
            //entityManager.persist(orderItem);
            orderItemList.add(orderItem);
        }

        order.setOrderItems(orderItemList);
        entityManager.persist(order);
        entityManager.flush();

        return OrderMapper.toDTO(order);

    }

    @Override
    public ServiceResponse<Void> completeOrder(String orderId) {

        int oId = Integer.parseInt(orderId.replaceAll(Validator.NON_DIGIT_PATTERN, ""));
        try {
            Order order = entityManager.find(Order.class, oId);
            if (order == null) {
                return ServiceResponse.<Void>builder().success(false).message("Order not found for ID: " + oId).build();
            }

            if (order.getStatus() != null && order.getStatus().getValue().equals(String.valueOf(Status.Type.COMPLETED))) {
                return ServiceResponse.<Void>builder().success(true).message("Order already completed").build();
            }

            //update stock
            List<OrderItem> orderItems = order.getOrderItems();

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) { // for loop dekak enne partial stock update nawattanna.
                    Stock stock = item.getStock();

                    if (stock == null) {
                        Status failedStock = entityManager
                                .createNamedQuery("Status.findByValue", Status.class)
                                .setParameter("value", "INACTIVE")
                                .getSingleResult();
                        order.setStatus(failedStock);
                        order.setEndTime(System.currentTimeMillis());
                        return ServiceResponse.<Void>builder().success(false).message("stock not found: ").build();
                    }
                    if (!stock.isStatus()) {
                        Status failedStock = entityManager
                                .createNamedQuery("Status.findByValue", Status.class)
                                .setParameter("value", "INACTIVE")
                                .getSingleResult();
                        order.setStatus(failedStock);
                        order.setEndTime(System.currentTimeMillis());
                        return ServiceResponse.<Void>builder().success(false).message("stock not active: " + stock.getId()).build();
                    }

                    int updatedQty = stock.getQty() - item.getQty();
                    if (updatedQty < 0) {
                        Status failedStock = entityManager
                                .createNamedQuery("Status.findByValue", Status.class)
                                .setParameter("value", "FAILED_OUT_OF_STOCK")
                                .getSingleResult();
                        order.setStatus(failedStock);
                        order.setEndTime(System.currentTimeMillis());
                        return ServiceResponse.<Void>builder().success(false).message("Insufficient stock for product: " + stock.getId()).build();
                    }
                }
                for (OrderItem item : orderItems) {
                    Stock stock = item.getStock();
                    stock.setQty(stock.getQty() - item.getQty());
                    entityManager.merge(stock);

                    //cache stock
                    inventoryTopicPublisher.publishInventoryUpdate(InventoryUpdateEvent
                            .builder()
                            .stockId(stock.getId())
                            .newQty(stock.getQty()- item.getQty())
                            .soldQty(item.getQty())
                            .unitPrice(stock.getPrice())
                            .userId(order.getUser().getId())
                            .userName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                            .productTitle(stock.getProduct().getTitle())
                            .productCategory(stock.getProduct().getCategory().getName())
                            .productBrand(stock.getProduct().getBrand().getName())
                            .build());
                }
            }

            //update order status
            Status completedStatus = entityManager.createNamedQuery("Status.findByValue", Status.class)
                    .setParameter("value", String.valueOf(Status.Type.COMPLETED))
                    .getSingleResult();
            order.setStatus(completedStatus);
            order.setEndTime(System.currentTimeMillis());
            entityManager.merge(order);

            //remove cart items
            List<Cart> cartList = entityManager.createQuery("FROM Cart c WHERE c.user=:user", Cart.class)
                    .setParameter("user", order.getUser())
                    .getResultList();

            for (Cart cart : cartList) {
                entityManager.remove(cart);
            }
            return ServiceResponse.<Void>builder().success(true).message("order completed :OrderId :" + order.getId()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<Void>builder().success(false).message("Order complete failed. OrderId :" + e.getMessage()).build();
        }
    }

    @Override
    public ServiceResponse<Void> failedOrder(String orderId) {

        System.out.println("fail order");
        int oId = Integer.parseInt(orderId.replaceAll(Validator.NON_DIGIT_PATTERN, ""));
        try {
            Order order = entityManager.find(Order.class, oId);
            if (order == null) {
                return ServiceResponse.<Void>builder().success(false).message("Order not found for Order ID: " + oId).build();
            }

            //idempotency check
            if (order.getStatus().getValue().equals(String.valueOf(Status.Type.REJECTED))) {
                return ServiceResponse.<Void>builder().success(true).message("Order already marked as 'failed': " + oId).build();
            }

            Status rejectedStatus = entityManager.createNamedQuery("Status.findByValue", Status.class)
                    .setParameter("value", String.valueOf(Status.Type.REJECTED)).getSingleResult();
            order.setStatus(rejectedStatus);
            order.setEndTime(System.currentTimeMillis());
            entityManager.merge(order);

            return ServiceResponse.<Void>builder().success(true).message("Order marked as 'failed' for ID: " + oId).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<Void>builder().success(false).message("Order marked as 'failed' failing failed due to " + e.getMessage()).build();
        }
    }

    public ServiceResponse<Void> verifyOrderDetails(String orderId) {
        int oId = Integer.parseInt(orderId.replaceAll(Validator.NON_DIGIT_PATTERN, ""));
        Order order = entityManager.find(Order.class, oId);
        if (order == null) {
            return ServiceResponse.<Void>builder().success(false).message("Incorrect Order details. Please check credentials!").build();
        }
        if (order.getStatus() != null && order.getStatus().getValue().equals(String.valueOf(Status.Type.COMPLETED))) {
            return ServiceResponse.<Void>builder().success(true).message("verified").build();
        }
        return ServiceResponse.<Void>builder().success(false).message("Order is not completed yet").build();
    }


}
