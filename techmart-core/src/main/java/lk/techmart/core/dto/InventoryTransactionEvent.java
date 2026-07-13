package lk.techmart.core.dto;

import lk.techmart.core.entity.InventoryTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionEvent implements Serializable {
    private Integer stockId;
    private Integer warehouseId;
    private Integer qty;
    private InventoryTransaction.InventoryType type;
    private String reason;
    private Integer userId;
}
