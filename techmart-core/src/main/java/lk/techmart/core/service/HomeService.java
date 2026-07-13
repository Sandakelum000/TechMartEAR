package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.ProductDTO;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Remote
public interface HomeService {
    ServiceResponse<List<ProductDTO>> getHomeProducts();
}
