package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.ProductDTO;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.entity.Product;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.mapper.AttributeMapper;
import lk.techmart.core.service.HomeService;
import lk.techmart.core.util.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class HomeServiceBean implements HomeService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<List<ProductDTO>> getHomeProducts() {

        try{
            List<Stock> stockList = entityManager.createQuery(
                    "SELECT DISTINCT s FROM Stock s " +
                            "JOIN FETCH s.product p " +
                            "LEFT JOIN FETCH p.images " +
                            "WHERE s.status = true " +
                            "AND s.qty > 0", Stock.class).getResultList();


            List<ProductDTO> productDTOList = AttributeMapper.productToDTO(stockList);
            return ServiceResponse.<List<ProductDTO>>builder()
                    .success(true)
                    .message("Product loading successfully.")
                    .data(productDTOList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<List<ProductDTO>>builder().success(false).message("Products loading failed").build();
        }


    }
}
