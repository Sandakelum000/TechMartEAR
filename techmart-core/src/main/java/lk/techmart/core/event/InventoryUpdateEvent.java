package lk.techmart.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateEvent implements Serializable {
    private Integer stockId;
    private Integer newQty;
    private Integer soldQty;
    private double unitPrice;
    private Integer userId;
    private String userName;
    private String productTitle;
    private String productCategory;
    private String productBrand;
}
