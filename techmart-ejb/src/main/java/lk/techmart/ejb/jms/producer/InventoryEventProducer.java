package lk.techmart.ejb.jms.producer;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import lk.techmart.core.annotation.Console;
import lk.techmart.core.dto.InventoryTransactionEvent;
import lk.techmart.ejb.event.Logger;

@Stateless
public class InventoryEventProducer {
    @Resource(lookup = "jms/myQueueConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/inventoryQueue")
    private Queue queue;

    @Inject
    private Event<String> loggerEvent;

    public void sendInventoryEvent(InventoryTransactionEvent event){
        try (JMSContext context = connectionFactory.createContext()) {
            context.createProducer().send(queue, event);

            loggerEvent.fire("Sending Inventory event:  "+event.toString());
        }
    }

}
