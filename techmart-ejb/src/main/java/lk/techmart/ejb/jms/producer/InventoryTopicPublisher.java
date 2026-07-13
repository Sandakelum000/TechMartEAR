package lk.techmart.ejb.jms.producer;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;
import lk.techmart.core.event.InventoryUpdateEvent;

@Stateless
public class InventoryTopicPublisher {
    @Resource(lookup = "jms/TechmartConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/techmartTopic")
    private Topic topic;

    public void publishInventoryUpdate(InventoryUpdateEvent event){
            try(JMSContext context = connectionFactory.createContext()){
                context.createProducer().send(topic,event);
            }
    }
}
