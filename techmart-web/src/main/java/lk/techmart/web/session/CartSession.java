package lk.techmart.web.session;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lk.techmart.core.dto.CartItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Named
@SessionScoped
public class CartSession implements Serializable {
    private List<CartItemDTO> items = new ArrayList<>();

    public void add(CartItemDTO item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }
}
