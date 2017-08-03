package eve.model.infra;

import java.util.List;

/**
 * Created by jesus on 12/7/16.
 */
public class Element {
    int x, y ;
    String mac ;
    String IPV4, IPV6 ;

    public Element () {
    }

    public static List<Element> getFromJSON(String json){
        return null ;
    }

    private static Element getFromJSON() {
        return null ;
    }
}
