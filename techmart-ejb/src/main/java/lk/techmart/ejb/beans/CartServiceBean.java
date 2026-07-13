package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.CartRequestDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.Cart;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.entity.User;
import lk.techmart.core.service.CartService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.core.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Stateless
public class CartServiceBean implements CartService {
    private static final String FILE_BASE_URL =
            "http://localhost:8080/techmart/api/files/";

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<List<CartItemDTO>> addToCartGuest(CartRequestDTO dto, List<CartItemDTO> sessionCartList) {
        try {
            if (dto == null || dto.getStockId() == null || dto.getQty() == null) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Invalid request").data(null).build();
            }
            if (!dto.getStockId().matches(Validator.IS_INTEGER) || !dto.getQty().matches(Validator.IS_INTEGER)) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Invalid Parameter request").data(null).build();
            }
            int stockId = Integer.parseInt(dto.getStockId());
            int requestQty = Integer.parseInt(dto.getQty());

            Stock stock = entityManager.find(Stock.class, stockId);
            if (stock == null) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Product not found").data(null).build();
            }
            if (requestQty > stock.getQty()) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Product quantity exceeded").data(null).build();
            }
            if (sessionCartList == null) {
                sessionCartList = new ArrayList<>();
            }

            CartItemDTO existingItem = null;

            for (CartItemDTO c : sessionCartList) {
                if (c.getStockId() == stockId) {
                    existingItem = c;
                    break;
                }
            }
            if (existingItem != null) {

                int newQty = existingItem.getQty() + requestQty;
                if (newQty > stock.getQty()) {
                    return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Product quantity exceeded").data(null).build();
                }
                existingItem.setQty(newQty);
                return ServiceResponse.<List<CartItemDTO>>builder()
                        .success(true).message("Cart updated successfully").data(sessionCartList).build();
            }

            CartItemDTO newItem = CartItemDTO.builder()
                    .cartId(Math.abs(UUID.randomUUID().hashCode()))
                    .stockId(stockId)
                    .qty(requestQty)
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
                    .build();
            sessionCartList.add(newItem);

            return ServiceResponse.<List<CartItemDTO>>builder()
                    .success(true).message("Product added to cart").data(sessionCartList).build();

        } catch (Exception e) {
            return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("System error while adding to cart").build();
        }
    }

    @Override
    public ServiceResponse<Void> addToCartUser(CartRequestDTO dto, UserSessionDTO user) {
        try {
            if (dto == null || dto.getStockId() == null || dto.getQty() == null) {
                return ServiceResponse.<Void>builder().success(false).message("Invalid request").data(null).build();
            }
            if (!dto.getStockId().matches(Validator.IS_INTEGER) || !dto.getQty().matches(Validator.IS_INTEGER)) {
                return ServiceResponse.<Void>builder().success(false).message("Invalid Parameter request").data(null).build();
            }
            int stockId = Integer.parseInt(dto.getStockId());
            int requestQty = Integer.parseInt(dto.getQty());

            User dbUser = entityManager.find(User.class, user.getId());
            if (dbUser == null) {
                return ServiceResponse.<Void>builder().success(false).message("User not found").build();
            }

            Stock stock = entityManager.find(Stock.class, stockId);
            if (stock == null) {
                return ServiceResponse.<Void>builder().success(false).message("Stock not found").build();
            }

            Cart existingCart = entityManager.createQuery("SELECT c FROM Cart c WHERE c.user=:user AND c.stock=:stock", Cart.class)
                    .setParameter("user", dbUser)
                    .setParameter("stock", stock)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingCart == null) {
                //new cart Item
                if (requestQty > stock.getQty()) {
                    return ServiceResponse.<Void>builder().success(false).message("Product quantity exceeded").build();
                }

                Cart newCart = Cart.builder().user(dbUser).stock(stock).qty(requestQty).build();
                entityManager.persist(newCart);
                return ServiceResponse.<Void>builder().success(true).message("Product added to the cart").build();
            }

            int newQty = existingCart.getQty() + requestQty;
            if (newQty > stock.getQty()) {
                return ServiceResponse.<Void>builder().success(false).message("Product quantity exceeded!").build();
            }

            existingCart.setQty(newQty);
            entityManager.merge(existingCart);
            return ServiceResponse.<Void>builder().success(true).message("User cart updated.").build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<Void>builder().success(false).message("Something went wrong. Please try again later.").build();
        }
    }

    @Override
    public ServiceResponse<List<CartItemDTO>> getAllUserCarts(UserSessionDTO user, List<CartItemDTO> sessionCart) {
        try {
            if (user == null) { // guest user
                if (sessionCart == null || sessionCart.isEmpty()) {
                    return ServiceResponse.<List<CartItemDTO>>builder()
                            .success(true)
                            .message("Your cart is empty")
                            .data(new ArrayList<>())
                            .build();
                }
                for (CartItemDTO item : sessionCart) {
                    Stock stock = entityManager.find(Stock.class, item.getStockId());
                    if (stock == null) {
                        continue;
                    }
                    item.setProductTitle(stock.getProduct().getTitle());
                    item.setImages(
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
                    );
                    item.setPrice(stock.getPrice());
                }

                return ServiceResponse.<List<CartItemDTO>>builder()
                        .success(true)
                        .message("Cart loaded successfully")
                        .data(sessionCart)
                        .build();
            }

            // logged user
            List<Cart> cartList = entityManager.createQuery(
                            "FROM Cart c WHERE c.user.id=:id",
                            Cart.class)
                    .setParameter("id", user.getId())
                    .getResultList();
            List<CartItemDTO> dtoList = new ArrayList<>();

            for (Cart cart : cartList) {
                Stock stock = cart.getStock();
                dtoList.add(
                        CartItemDTO.builder()
                                .cartId(cart.getId())
                                .stockId(stock.getId())
                                .productTitle(stock.getProduct().getTitle())
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
                                .qty(cart.getQty())
                                .price(stock.getPrice())
                                .build()
                );
            }

            return ServiceResponse.<List<CartItemDTO>>builder()
                    .success(true)
                    .message(dtoList.isEmpty()
                            ? "Your cart is empty"
                            : "Cart loaded successfully")
                    .data(dtoList)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Failed to load cart").build();
        }
    }

    @Override
    public ServiceResponse<List<CartItemDTO>> deleteCartItem(String cartId, UserSessionDTO user, List<CartItemDTO> sessionCart) {
        try {
            if (cartId == null || cartId.isBlank() || !cartId.matches(Validator.IS_INTEGER)) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Invalid Id format").build();
            }

            int cId = Integer.parseInt(cartId);
            if (user == null) { //Guest
                if (sessionCart == null) {
                    sessionCart = new ArrayList<>();
                }

                boolean removed = sessionCart.removeIf(cartItemDTO -> cartItemDTO.getCartId() == cId);

                if (!removed) {
                    return ServiceResponse.<List<CartItemDTO>>builder()
                            .success(false)
                            .message("Cart item not found")
                            .data(sessionCart)
                            .build();
                }
                return ServiceResponse.<List<CartItemDTO>>builder()
                        .success(true)
                        .message("Cart item deleted")
                        .data(sessionCart)
                        .build();
            }

            //logged user
            Cart existingCart = entityManager.createQuery("FROM Cart c WHERE c.id=:cartId AND c.user.id=:userId", Cart.class)
                    .setParameter("cartId", cId)
                    .setParameter("userId", user.getId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingCart == null) {
                return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Cart item not found").build();
            }
            entityManager.remove(existingCart);
            return ServiceResponse.<List<CartItemDTO>>builder().success(true).message("Cart item deleted").build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<List<CartItemDTO>>builder().success(false).message("Unable to delete cart item").build();
        }
    }

}
