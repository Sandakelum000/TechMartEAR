package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.CartRequestDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Remote
public interface CartService {
    ServiceResponse<List<CartItemDTO>> addToCartGuest(CartRequestDTO dto, List<CartItemDTO> sessionCart);
    ServiceResponse<Void> addToCartUser(CartRequestDTO dto, UserSessionDTO user);
    ServiceResponse<List<CartItemDTO>> getAllUserCarts(UserSessionDTO user,List<CartItemDTO> sessionCart);
    ServiceResponse<List<CartItemDTO>> deleteCartItem(String cartId, UserSessionDTO user,List<CartItemDTO> sessionCart);
}
