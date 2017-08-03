package eve.wuti.jpa;

/**
 * User: jrubio
 * Date: 13-jun-2013
 */
public class Insert extends Change {
    public Insert(Object obj) {
        super(obj, Type.insert);
    }
}
