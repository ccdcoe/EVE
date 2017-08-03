package eve.wuti.jpa;

/**
 * User: jrubio
 * Date: 13-jun-2013
 */
public class Delete extends Change {
    public Delete(Object obj) {
        super(obj, Type.delete);
    }
}