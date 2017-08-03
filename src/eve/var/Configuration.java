package eve.var;

import eve.wuti.http.WebXML;

/**
 * Created by jesus on 12/6/16.
 */
public class Configuration {
    public String getUrlToEVE() {
        return WebXML.readParameter("URL_TO_EVE");
    }
}
