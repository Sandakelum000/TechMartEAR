package lk.techmart.ejb.jms.config;

import jakarta.ejb.Stateless;
import lk.techmart.core.messaging.MessageBrokerType;

@Stateless
public class SystemConfigService {
    public MessageBrokerType getActiveBroker(){
        return MessageBrokerType.ACTIVEMQ;
    }
}
