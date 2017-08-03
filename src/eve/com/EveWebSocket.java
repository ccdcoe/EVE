package eve.com;

import eve.ctrl.Control;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


/**
 * Created by jesus on 11/16/16.
 */
@SuppressWarnings("UnusedDeclaration")
@ServerEndpoint(value = "/ws/{network}")
public class EveWebSocket {
    public static enum Client {yellow, red}

    private boolean valid;
    private Session wsSession;
    private Client client = Client.red ;


    @OnOpen
    public void onOpen(@PathParam("network") String network, Session session, EndpointConfig config) {
        System.out.println("New client connection: " + session.getId() + " for network = " + network);

        valid = true;
        wsSession = session;
        if (network != null && network.trim().toLowerCase().equals("gt")) client = Client.yellow;
        
        Control.get().newEveSocket(this);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message from " + session.getId() + ": " + message);
    }


    @OnClose
    public void onClose(Session session) {
        valid = false;
        System.out.println("Client session " + session.getId() + " closed.");
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        valid = false;
        System.out.println("Client session error:  " + session.getId());
    }

    public void sendText(String text) throws Exception {
        if (!valid) return;
        wsSession.getBasicRemote().sendText(text);
    }

    public Client getClient() {
        return client;
    }

    public boolean isValid() {
        return valid;
    }
}