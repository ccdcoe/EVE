package eve.wuti.jpa;

/**
 * User: jrubio
 * Date: 13-jun-2013
 */
public class Update extends Change {
    public Update(Object obj) {
        super(obj, Type.update);
    }
}