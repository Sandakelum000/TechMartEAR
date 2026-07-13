package lk.techmart.core.mapper;

import lk.techmart.core.dto.*;
import lk.techmart.core.entity.Brand;
import lk.techmart.core.entity.Category;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.entity.Warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeMapper {
    private static final String FILE_BASE_URL =
            "http://localhost:8080/techmart/api/files/";

    public static List<BrandDTO> brandToDTO(List<Brand> brands) {
        if (brands == null) {
            return Collections.emptyList();
        }
        return brands.stream()
                .map(brand -> BrandDTO.builder()
                        .id(brand.getId())
                        .name(brand.getName())
                        .build())
                .toList();
    }

    public static List<CategoryDTO> categoryToDTO(List<Category> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public static List<ProductDTO> productToDTO(List<Stock> stocks) {
        if (stocks == null) {
            return Collections.emptyList();
        }
        return stocks.stream()
                .map(stock -> ProductDTO.builder()
                        .stockId(stock.getId())
                        .productId(stock.getProduct().getId())
                        .brandId(stock.getProduct().getBrand().getId())
                        .brandName(stock.getProduct().getBrand().getName())
                        .categoryId(stock.getProduct().getCategory().getId())
                        .categoryName(stock.getProduct().getCategory().getName())
                        .qty(stock.getQty())
                        .title(stock.getProduct().getTitle())
                        .price(stock.getPrice())
                        .images(
                                stock.getProduct().getImages() == null
                                        ? Collections.emptyList()
                                        : stock.getProduct().getImages().stream()
                                        .map(img -> {
                                            String cleaned = img.startsWith("/uploads/")
                                                    ?img.substring("/uploads/".length())
                                                    :img;
                                            return FILE_BASE_URL + "/" + cleaned;
                                        })
                                        .toList()
                        )
                        .build())
                .toList();
    }

    public static List<WarehouseDTO> warehouseToDTO(List<Warehouse> warehouses) {
        if (warehouses == null) {
            return Collections.emptyList();
        }
        return warehouses.stream()
                .map(warehouse -> WarehouseDTO.builder()
                        .id(warehouse.getId())
                        .name(warehouse.getName())
                        .location(warehouse.getLocation()).build())
                .toList();
    }
}
