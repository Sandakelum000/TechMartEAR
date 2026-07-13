package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.PaymentHistoryDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.Order;
import lk.techmart.core.entity.OrderItem;
import lk.techmart.core.entity.Status;
import lk.techmart.core.entity.User;
import lk.techmart.core.service.PaymentService;
import lk.techmart.core.util.ServiceResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class PaymentServiceBean implements PaymentService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<List<PaymentHistoryDTO>> userPaymentHistory(UserSessionDTO userDTO) {
        try{
            if(userDTO == null){
                return ServiceResponse.<List<PaymentHistoryDTO>>builder().success(false).message("Please login first").build();
            }
            User user  = entityManager.find(User.class, userDTO.getId());
            if(user == null){
                return ServiceResponse.<List<PaymentHistoryDTO>>builder().success(false).message("User not found").build();
            }
            List<Order> orderList = entityManager.createQuery("SELECT DISTINCT o FROM Order o " +
                            "JOIN FETCH o.orderItems oi " +
                            "JOIN FETCH oi.stock s " +
                            "JOIN FETCH s.product " +
                            "JOIN o.status st " +
                            "WHERE o.user.id=:userId " +
                            "AND st.value =:status " +
                            "ORDER BY o.createdAt DESC",Order.class)
                    .setParameter("userId", user.getId())
                    .setParameter("status", Status.Type.COMPLETED.name())
                    .getResultList();

            if (orderList.isEmpty()) {
                return ServiceResponse.<List<PaymentHistoryDTO>>builder()
                        .success(true)
                        .message("No payment history found")
                        .data(Collections.emptyList())
                        .build();
            }

            List<PaymentHistoryDTO> paymentHistoryList = new ArrayList<>();
            for(Order order : orderList){
                for(OrderItem orderItem: order.getOrderItems()){
                    paymentHistoryList.add(
                            PaymentHistoryDTO.builder()
                                    .productTitle(orderItem.getStock().getProduct().getTitle())
                                    .qty(orderItem.getQty())
                                    .unitPrice(orderItem.getStock().getPrice())
                                    .createdAt(order.getCreatedAt())
                                    .status(order.getStatus().getValue())
                                    .build()
                    );
                }
            }
            return ServiceResponse.<List<PaymentHistoryDTO>>builder().success(true).message("Payment history retrieved successfully.").data(paymentHistoryList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<List<PaymentHistoryDTO>>builder()
                    .success(false)
                    .message("Data retrieving failed due to server error.")
                    .build();
        }

    }
}
