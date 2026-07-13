package lk.techmart.ejb.jms.consumer;

import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lk.techmart.core.dto.InventoryTransactionEvent;
import lk.techmart.core.service.InventoryService;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "jms/inventoryQueue"),
                @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakarta.jms.Queue")
        }
)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InventoryMDB implements MessageListener {

    @EJB
    private InventoryService inventoryService;

    @Inject
    private Event<String> loggerEvent;

    @Override
    public void onMessage(Message message) {
        try{
            InventoryTransactionEvent event = message.getBody(InventoryTransactionEvent.class);
            inventoryService.processInventoryTransaction(event);

            loggerEvent.fire("Inventory MDB receive event: "+event.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
