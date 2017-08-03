package eve.ctrl;

import eve.model.attack.Attack;
import eve.model.event.Event;
import eve.model.event.Source;
import eve.model.event.Target;
import eve.wuti.jpa.DAO;
import eve.wuti.jpa.Insert;
import eve.wuti.jpa.Update;

import java.util.LinkedList;

/**
 * Created by jesus on 12/2/16.
 */
class Adam {
    public static class Result {
        public static enum Type {update, newAttack}

        public Type type;
        public Attack attack;

        public Result(Type type, Attack attack) {
            this.type = type;
            this.attack = attack;
        }
    }

    private LinkedList<Attack> lastAttacks = new LinkedList<>();

    private static final int MAX_MILLIS_SEPARATION = 1000 * 3; // last number is "seconds"

    public Adam() {
    }

    public Result newEvent(Event event)  {

        // Nothing yields nothing
        if (event == null) return null;

        // Source, target
        Source sourceObs = event.getSource();
        Target targetObs = event.getTarget();

        // Looking for the event within last events
        for (Attack attack : lastAttacks) {
            if (attack == null) continue;

            if (Math.abs(attack.getLastNotificationDate().getTime() - event.getNotificationDate().getTime()) > MAX_MILLIS_SEPARATION) continue;

            // Source and target
            Source source = attack.getSource();
            Target target = attack.getTarget();

            // Compassion
            if ((source != null && source.isEquivalentTo(sourceObs)) && (target != null && target.isEquivalentTo(targetObs))) return update(attack, event);
            if ((source == null && sourceObs == null) && (target != null && target.isEquivalentTo(targetObs))) return update(attack, event);
            if ((source != null && source.isEquivalentTo(sourceObs)) && (target == null && targetObs == null)) return update(attack, event);
        }

        // New event!
        Attack newAttack = new Attack(event);
        try {
            DAO.get().execute(new Insert(newAttack));
        } catch (Exception e) {
            System.out.println("Attack could not be inserted into database: " + e.getMessage());
            System.out.println("Attack data: " + newAttack.toJSON());
            e.printStackTrace();
        }
        lastAttacks.add(newAttack);
        return new Result(Result.Type.newAttack, newAttack);
    }

    private Result update(Attack attack, Event event) {
        attack.addEvent(event);

        try {
            DAO.get().execute(new Update(attack));
        } catch (Exception e) {
            System.out.println("Attack could not be updated: " + e.getMessage());
            System.out.println("Attack data: " + attack.toJSON());
            e.printStackTrace();
        }

        return new Result(Result.Type.update, attack);
    }
}
