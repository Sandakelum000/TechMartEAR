package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.OrderDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.Order;
import lk.techmart.core.entity.User;
import lk.techmart.core.util.ServiceResponse;

@Remote
public interface OrderService {
    OrderDTO createPendingOrder(UserSessionDTO user);
    ServiceResponse<Void> completeOrder(String orderId);
    ServiceResponse<Void> failedOrder(String orderId);
    ServiceResponse<Void> verifyOrderDetails(String orderId);
}
