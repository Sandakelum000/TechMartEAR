package lk.techmart.core.dto;

import lk.techmart.core.entity.InventoryTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryTransactionDTO implements Serializable {
    private int id;
    private InventoryTransaction.InventoryType type;
    private int qty;
    private String reference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
