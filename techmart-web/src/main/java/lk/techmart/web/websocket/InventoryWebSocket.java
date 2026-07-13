package lk.techmart.web.websocket;

import jakarta.websocket.Session;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/inventory")
public class InventoryWebSocket {

    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session){
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session){
        SESSIONS.remove(session);
    }

    public static void broadcast(String message){
        for(Session session :SESSIONS){
            try{
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
