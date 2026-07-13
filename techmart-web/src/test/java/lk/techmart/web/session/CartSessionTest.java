package lk.techmart.web.session;

import lk.techmart.core.dto.CartItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartSessionTest {
    private CartSession cartSession;

    @BeforeEach
    void setUp() {
        cartSession = new CartSession();
    }

    @Test
    void testAddItem() {

        CartItemDTO item = CartItemDTO.builder()
                .cartId(1)
                .productTitle("iPhone 15")
                .qty(2)
                .price(1500.0)
                .stockId(10)
                .images(List.of("img1.jpg", "img2.jpg"))
                .build();

        cartSession.add(item);

        assertEquals(1, cartSession.getItems().size());

        CartItemDTO saved = cartSession.getItems().get(0);

        assertEquals(1, saved.getCartId());
        assertEquals("iPhone 15", saved.getProductTitle());
        assertEquals(2, saved.getQty());
        assertEquals(1500.0, saved.getPrice());
        assertEquals(10, saved.getStockId());
        assertEquals(2, saved.getImages().size());
    }

    @Test
    void testClearCart() {

        CartItemDTO item = CartItemDTO.builder()
                .cartId(1)
                .productTitle("MacBook Pro")
                .qty(1)
                .price(2500.0)
                .stockId(5)
                .images(List.of("mac1.jpg"))
                .build();

        cartSession.add(item);
        cartSession.clear();

        assertTrue(cartSession.getItems().isEmpty());
    }

    @Test
    void testMultipleItems() {

        CartItemDTO item1 = CartItemDTO.builder()
                .cartId(1)
                .productTitle("Phone")
                .qty(1)
                .price(1000.0)
                .stockId(11)
                .images(List.of("p1.jpg"))
                .build();

        CartItemDTO item2 = CartItemDTO.builder()
                .cartId(2)
                .productTitle("Laptop")
                .qty(2)
                .price(3000.0)
                .stockId(12)
                .images(List.of("l1.jpg"))
                .build();

        cartSession.add(item1);
        cartSession.add(item2);

        assertEquals(2, cartSession.getItems().size());
    }
}
