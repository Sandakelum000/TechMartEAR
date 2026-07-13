package lk.techmart.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lk.techmart.core.event.InventoryBroadcastEvent;

@ApplicationScoped
public class InventoryWebSocketBridge {
    private final ObjectMapper mapper = new ObjectMapper();

    public void onInventoryUpdate(@Observes InventoryBroadcastEvent event){
       try{
           String json = mapper.writeValueAsString(event);
           InventoryWebSocket.broadcast(json);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}
