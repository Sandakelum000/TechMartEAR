package lk.techmart.core.dto;

import lk.techmart.core.entity.OrderItem;
import lk.techmart.core.entity.Status;
import lk.techmart.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private int id;
    private int userId;
    private String status;
    private List<OrderItemDTO> orderItems;
}
