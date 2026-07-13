package lk.techmart.ejb.jms.producer;

import jakarta.annotation.Resource;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import lk.techmart.core.annotation.Order;
import lk.techmart.core.dto.PaymentEventDTO;
import lk.techmart.core.service.CheckoutMessageService;

@Stateless(name = "payaraProducer")
public class PayaraCheckoutMessageProducer implements CheckoutMessageService {

    @Resource(lookup = "jms/myQueueConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/myQueue")
    private Queue queue;

    @Inject
    @Order
    private Event<String> logEvent;

    public void sendPaymentEvent(String orderId, int statusCode) {
        PaymentEventDTO messagePayload = new PaymentEventDTO(orderId, statusCode);

        try (JMSContext context = connectionFactory.createContext()) {
            context.createProducer().send(queue, messagePayload);
        }
        logEvent.fire("Payment message queued for Order ID: " + orderId);
    }
}
