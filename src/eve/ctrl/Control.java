package eve.ctrl;

import eve.com.EveWebSocket;
import eve.model.attack.Attack;
import eve.model.event.Event;
import eve.model.infra.Element;
import eve.model.infra.Host;
import eve.wuti.jpa.DAO;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jesus on 11/16/16.
 */
public class Control {

    // Web sockets
    private List<EveWebSocket> webSockets = new LinkedList<>();

    // Main data handlers
    private Adam adam = new Adam();
    private RedTeamNetwork redTeamNetwork = new RedTeamNetwork() ;


    // This instance
    private static Control control;
    public static Control get() {
        if (control == null) control = new Control();
        return control;
    }

    private Control() {
    }

    // Handling web sockets
    public synchronized void newEveSocket(EveWebSocket eveWebSocket) {
        if (eveWebSocket == null || webSockets.contains(eveWebSocket)) return;
        webSockets.add(eveWebSocket);

        if (eveWebSocket.getClient().equals(EveWebSocket.Client.red)) {
            try {
                eveWebSocket.sendText(redTeamNetwork.toJSON());
            } catch (Exception e) {
                System.out.println("Error while sending info to rtn.jsp: " + e.getMessage());
            }
        }

        else if (eveWebSocket.getClient().equals(EveWebSocket.Client.yellow)) {
            try {
                List<Attack> attacks = DAO.get().getList(Attack.class) ;
                if (attacks != null) for (Attack attack : attacks) {
                    String json = attack.toJSON() ;
                    eveWebSocket.sendText("{\"attack\":" + json + ", \"type\":\"historic\"}");
                }

            } catch (Exception e) {
                System.out.println("Error while sending info to rtn.jsp: " + e.getMessage());
            }
        }
    }

    // Handling incoming events
    public synchronized void newEvent(Event event)  {

        // Transform into event (either existing or new)
        Adam.Result result = adam.newEvent(event);
        if (result == null) return;

        // Update red team network
        redTeamNetwork.newEvent(result.attack, result.type == Adam.Result.Type.update);

        // Send result (while taking care of faulty clients)
        List<EveWebSocket> faultySockets = new LinkedList<>();
        for (EveWebSocket webSocket : webSockets) {

            if (webSocket != null && webSocket.isValid())
                try {

                    if (webSocket.getClient() == EveWebSocket.Client.yellow) {

                        String json = result.attack.toJSON() ;
                        String type = Adam.Result.Type.update.equals(result.type) ? "update" : "last";

                        webSocket.sendText("{\"attack\":" + json + ", \"type\":\"" + type + "\"}");
                    }

                    else if (webSocket.getClient() == EveWebSocket.Client.red) {
                        webSocket.sendText(redTeamNetwork.toJSON());
                    }

                } catch (Exception e) {
                    faultySockets.add(webSocket);
                    e.printStackTrace();
                    System.out.println("Error while sending to web client : " + e.getMessage());
                }
            else
                faultySockets.add(webSocket);
        }

        if (faultySockets.size() > 0) webSockets.removeAll(faultySockets);
    }

    public void newElements(List<Element> elements) {

    }
}
