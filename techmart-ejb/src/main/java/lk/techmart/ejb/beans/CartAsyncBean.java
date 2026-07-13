package lk.techmart.ejb.beans;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.annotation.Console;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.Cart;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.entity.User;
import lk.techmart.core.service.CartAsyncService;
import lk.techmart.core.service.CartService;

import java.util.List;

@Stateless
public class CartAsyncBean implements CartAsyncService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Inject
    @Console
    private Event<String> logEvent;

    @Asynchronous
    public void mergeCarts(UserSessionDTO sessionUser, List<CartItemDTO> sessionCart) {
        long mergeStart = System.currentTimeMillis();

        try {
            // cart merging start
            if (sessionUser == null || sessionCart == null || sessionCart.isEmpty()) {
                return;
            }
            User dbUser = entityManager.find(User.class, sessionUser.getId());
            if (dbUser == null) {
                return;
            }
            for (CartItemDTO c : sessionCart) {
                Stock stock = entityManager.find(Stock.class, c.getStockId());
                if (stock == null) {
                    continue;
                }
                Cart existingCart = entityManager.createQuery(
                                "SELECT c FROM Cart c WHERE c.user=:user AND c.stock=:stock",
                                Cart.class)
                        .setParameter("user", dbUser)
                        .setParameter("stock", stock)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (existingCart == null) {
                    if (c.getQty() > stock.getQty()) {
                        continue;
                    }
                    Cart newCart = Cart.builder()
                            .user(dbUser)
                            .stock(stock)
                            .qty(c.getQty())
                            .build();
                    entityManager.persist(newCart);
                    logEvent.fire("Cart added: user " + dbUser.getId() + " stock " + stock.getId());
                    continue;
                }

                int newQty = existingCart.getQty() + c.getQty();
                if (newQty > stock.getQty()) {
                    continue;
                }
                existingCart.setQty(newQty);
                entityManager.merge(existingCart);
                logEvent.fire("Cart updated: user " + dbUser.getId() + " stock " + stock.getId());
            }

            logEvent.fire("Cart merge completed in " + (System.currentTimeMillis() - mergeStart) + " ms");
        } catch (Exception e) {
            logEvent.fire("Cart Merging failed...");
            e.printStackTrace();
        }
    }
}
