package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDataDTO implements Serializable {
    private int stockId;
    private double price;
    private double qty;
    private boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductDTO product;
    private WarehouseDTO warehouse;
    private List<InventoryTransactionDTO> inventoryTransactionDTOList;
}
