package lk.techmart.core.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryBroadcastEvent {
    @Getter
    private Integer stockId;
    @Getter
    private Integer newQty;
    @Getter
    private Integer soldQty;
    @Getter
    private double unitPrice;
    @Getter
    private Integer userId;
    @Getter
    private String userName;
    @Getter
    private String productTitle;
    @Getter
    private String productCategory;
    @Getter
    private String productBrand;
}
