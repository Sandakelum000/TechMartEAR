package lk.techmart.ejb.jms.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import lk.techmart.core.dto.PaymentEventDTO;
import lk.techmart.core.service.CheckoutMessageService;
import org.apache.activemq.ActiveMQConnectionFactory;

@Stateless(name = "activeMQProducer")
public class ActiveMQCheckoutMessageProducer implements CheckoutMessageService {

    private static final String BROKER_URL = "tcp://localhost:61616";
    @Override
    public void sendPaymentEvent(String orderId, int statusCode) {


        try {
            ActiveMQConnectionFactory factory =
                    new ActiveMQConnectionFactory(ActiveMQCheckoutMessageProducer.BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("checkoutQueue");
            MessageProducer producer = session.createProducer(queue);

            PaymentEventDTO payload = new PaymentEventDTO(orderId,statusCode);

            String json = new ObjectMapper().writeValueAsString(payload);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException | JsonProcessingException e) {
            throw new RuntimeException("ActiveMQ send failed",e);
        }
    }
}
