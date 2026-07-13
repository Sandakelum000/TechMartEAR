package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO implements Serializable {
    private int stockId;
    private int productId;
    private int qty;
    private double price;
    private String createdAt;
}
