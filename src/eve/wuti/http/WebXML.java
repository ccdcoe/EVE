package eve.wuti.http;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Creado por Jesus el 15/7/15.
 */
public class WebXML {
    public static <T> T readParameter(String parameter) {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            //noinspection unchecked
            return (T) env.lookup(parameter);
        } catch (Exception e) {
            return null;
        }
    }

}
