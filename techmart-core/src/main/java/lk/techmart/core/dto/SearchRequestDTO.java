package lk.techmart.core.dto;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private String brandName;
    private String categoryName;
    private Double priceStart;
    private Double priceEnd;
    private String sortBy;
    private String searchText;
    private int offset;
}
