package lk.techmart.ejb.jms.factory;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lk.techmart.core.messaging.MessageBrokerType;
import lk.techmart.core.service.CheckoutMessageService;

@Stateless
public class CheckoutMessageFactory {
    @EJB(beanName = "payaraProducer")
    private CheckoutMessageService payara;

    @EJB(beanName = "activeMQProducer")
    private CheckoutMessageService activeMQ;


    public CheckoutMessageService getProducer(MessageBrokerType brokerType){

        return switch (brokerType){
            case ACTIVEMQ -> activeMQ;
            default -> payara;
        };
    }

}
