package lk.techmart.ejb.jms.consumer;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lk.techmart.core.event.InventoryBroadcastEvent;
import lk.techmart.core.event.InventoryUpdateEvent;
import lk.techmart.core.service.InventorySyncService;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destinationLookup",
                propertyValue = "jms/techmartTopic"
        ),
        @ActivationConfigProperty(
                propertyName = "destinationType",
                propertyValue = "jakarta.jms.Topic"
        )
})
public class InventoryTopicListener implements MessageListener {

    @Inject
    private Event<InventoryBroadcastEvent> event;

    @Override
    public void onMessage(Message message) {
        try {
            InventoryUpdateEvent body = message.getBody(InventoryUpdateEvent.class);
            event.fire(InventoryBroadcastEvent
                    .builder()
                    .stockId(body.getStockId())
                    .newQty(body.getNewQty())
                    .soldQty(body.getSoldQty())
                    .unitPrice(body.getUnitPrice())
                    .userId(body.getUserId())
                    .userName(body.getUserName())
                    .productTitle(body.getProductTitle())
                    .productCategory(body.getProductCategory())
                    .productBrand(body.getProductBrand())
                    .build());

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
