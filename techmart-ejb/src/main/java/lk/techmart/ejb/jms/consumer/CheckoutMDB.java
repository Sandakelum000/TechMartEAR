package lk.techmart.ejb.jms.consumer;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import lk.techmart.core.annotation.Order;
import lk.techmart.core.dto.PaymentEventDTO;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.util.PayHereUtil;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.ejb.event.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "jms/myQueue"),
                @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakarta.jms.Queue")
        }
)
public class CheckoutMDB implements MessageListener {

    @EJB
    private OrderService orderService;

    @Inject
    @Order
    private Event<String> logEvent;

    @Override
    public void onMessage(Message message) {
        try {
                PaymentEventDTO paymentData = message.getBody(PaymentEventDTO.class);

                String orderId = paymentData.getOrderId();
                int statusCode = paymentData.getStatusCode();

                logEvent.fire("MDB processing for Order ID: " + orderId);


                if (statusCode == PayHereUtil.PAYMENT_SUCCESS) {
                    ServiceResponse<Void> response = orderService.completeOrder(orderId);
                    logEvent.fire(response.getMessage());
                } else {
                    ServiceResponse<Void> response = orderService.failedOrder(orderId);
                    logEvent.fire(response.getMessage());
                }

        } catch (JMSException e) {
            System.err.println("Error processing JMS Message inside CheckoutMDB");
            throw new RuntimeException(e);
        }
    }
}
