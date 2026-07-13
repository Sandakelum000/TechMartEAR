package lk.techmart.core.service;

import jakarta.ejb.Local;

@Local
public interface CheckoutMessageService {
    void sendPaymentEvent(String orderId, int statusCode);
}
