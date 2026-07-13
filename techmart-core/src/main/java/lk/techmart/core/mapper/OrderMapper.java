package lk.techmart.core.mapper;

import lk.techmart.core.dto.OrderDTO;
import lk.techmart.core.dto.OrderItemDTO;
import lk.techmart.core.entity.Order;
import lk.techmart.core.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().getValue());
        dto.setUserId(order.getUser().getId());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                itemDTOs.add(toItemDTO(item));
            }
        }

        dto.setOrderItems(itemDTOs);

        return dto;
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        if (item == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setQty(item.getQty());

        if (item.getStock() != null) {
            dto.setStockId(item.getStock().getId());
            dto.setProductTitle(item.getStock().getProduct().getTitle());
            dto.setPrice(item.getStock().getPrice());
        }

        return dto;
    }
}
