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
public class ProductResponseDTO implements Serializable {
    private List<BrandDTO> brandList;
    private List<CategoryDTO> categoryList;
    private List<ProductDTO> productList;
    private List<WarehouseDTO> warehouseList;
    private double minPrice;
    private double maxPrice;
    private int maxResult;
    private Long allProductCount;
}
