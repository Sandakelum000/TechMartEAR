package lk.techmart.core.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
public class AdminOrderSummeryDTO implements Serializable {
    private int orderId;
    private String status;
    private int userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long performance;
}
