package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.dto.SearchRequestDTO;
import lk.techmart.core.util.ServiceResponse;

@Remote
public interface AdvanceSearchService {
    ServiceResponse<ProductResponseDTO> getAllProducts();
    ServiceResponse<ProductResponseDTO> advanceSearch(SearchRequestDTO request);
}
