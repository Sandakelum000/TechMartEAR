package lk.techmart.ejb.jms.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.*;
import lk.techmart.core.annotation.Order;
import lk.techmart.core.dto.PaymentEventDTO;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.util.PayHereUtil;
import lk.techmart.core.util.ServiceResponse;
import org.apache.activemq.ActiveMQConnectionFactory;

@Singleton
@Startup
public class ActiveMQCheckoutConsumer{
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "checkoutQueue";

    @EJB
    private OrderService orderService;

    @Inject
    @Order
    private Event<String> logEvent;

    private Connection connection;
    @PostConstruct
    private void init(){
        try{
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory(BROKER_URL);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE_NAME);

            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(message -> {
                try {
                    String json = message.getBody(String.class);
                    PaymentEventDTO paymentData = new ObjectMapper().readValue(json, PaymentEventDTO.class);

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
                } catch (JMSException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutDown() {
        if(connection != null){
            try {
                connection.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
