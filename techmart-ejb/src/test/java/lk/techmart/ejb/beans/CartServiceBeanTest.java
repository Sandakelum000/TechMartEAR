package lk.techmart.ejb.beans;

import jakarta.persistence.EntityManager;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.CartRequestDTO;
import lk.techmart.core.entity.Product;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.util.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceBeanTest {
    @InjectMocks
    private CartServiceBean bean;

    @Mock
    private EntityManager entityManager;

    @Test
    void addToCartGuest_shouldReturnInvalidRequest_WhenDtoIsNull() {

        ServiceResponse<List<CartItemDTO>> response =
                bean.addToCartGuest(null, new ArrayList<>());

        assertFalse(response.isSuccess());
        assertEquals("Invalid request", response.getMessage());
        assertNull(response.getData());

        verifyNoInteractions(entityManager);
    }

    @Test
    void addToCartGuest_shouldRejectInvalidParameters() {

        CartRequestDTO dto = new CartRequestDTO();
        dto.setStockId("ABC");
        dto.setQty("2");

        ServiceResponse<List<CartItemDTO>> response =
                bean.addToCartGuest(dto, new ArrayList<>());

        assertFalse(response.isSuccess());
        assertEquals("Invalid Parameter request", response.getMessage());

        verifyNoInteractions(entityManager);
    }

    @Test
    void addToCartGuest_shouldReturnProductNotFound() {

        CartRequestDTO dto = new CartRequestDTO();
        dto.setStockId("1");
        dto.setQty("2");

        when(entityManager.find(Stock.class, 1))
                .thenReturn(null);

        ServiceResponse<List<CartItemDTO>> response =
                bean.addToCartGuest(dto, new ArrayList<>());

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());

        verify(entityManager).find(Stock.class, 1);
    }

    @Test
    void addToCartGuest_shouldRejectWhenQuantityExceedsStock() {

        CartRequestDTO dto = new CartRequestDTO();
        dto.setStockId("1");
        dto.setQty("5");

        Stock stock = new Stock();
        stock.setQty(2);

        when(entityManager.find(Stock.class, 1))
                .thenReturn(stock);

        ServiceResponse<List<CartItemDTO>> response =
                bean.addToCartGuest(dto, new ArrayList<>());

        assertFalse(response.isSuccess());
        assertEquals("Product quantity exceeded", response.getMessage());
    }

    @Test
    void addToCartGuest_shouldAddNewItemSuccessfully() {

        CartRequestDTO dto = new CartRequestDTO();
        dto.setStockId("1");
        dto.setQty("2");

        Product product = new Product();
        product.setTitle("iPhone 15");
        product.setImages(List.of(
                "/uploads/img1.jpg",
                "/uploads/img2.jpg"
        ));

        Stock stock = new Stock();
        stock.setId(1);
        stock.setQty(10);
        stock.setPrice(2500.00);
        stock.setProduct(product);

        when(entityManager.find(Stock.class, 1))
                .thenReturn(stock);

        List<CartItemDTO> sessionCart = new ArrayList<>();

        ServiceResponse<List<CartItemDTO>> response =
                bean.addToCartGuest(dto, sessionCart);

        assertTrue(response.isSuccess());
        assertEquals("Product added to cart", response.getMessage());

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());

        CartItemDTO item = response.getData().get(0);

        assertEquals(1, item.getStockId());
        assertEquals(2, item.getQty());

        assertTrue(item.getCartId() > 0);

        assertEquals(2, item.getImages().size());

        assertEquals(
                "http://localhost:8080/techmart/api/files//img1.jpg",
                item.getImages().get(0)
        );

        assertEquals(
                "http://localhost:8080/techmart/api/files//img2.jpg",
                item.getImages().get(1)
        );

        verify(entityManager).find(Stock.class, 1);
    }
}
