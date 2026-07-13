package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO implements Serializable {
    private int productId;
    private int categoryId;
    private String categoryName;
    private int brandId;
    private String brandName;
    private String title;
    private String description;
    private double price;
    private int warehouseId;
    private int qty;
    private List<String> images;
    private int stockId;
    private List<StockDTO> stockDTOList;
    private String createdAt;
}
