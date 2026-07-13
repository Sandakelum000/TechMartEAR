package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.UserSessionDTO;

import java.util.List;

@Remote
public interface CartAsyncService {
    void mergeCarts(UserSessionDTO user, List<CartItemDTO> sessionCart);
}
