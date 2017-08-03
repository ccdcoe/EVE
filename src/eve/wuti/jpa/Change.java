package eve.wuti.jpa;

import javax.persistence.EntityManager;

/**
 * User: jrubio
 * Date: 03-jun-2013
 */
public class Change<T> {

    public enum Type {
        insert {
            @Override
            <T> T execute(T t, EntityManager em) throws Exception {
                em.persist(t);
                return t;
            }
        },
        merge {
            @Override
            <T> T execute(T t, EntityManager em) throws Exception {
                em.merge(t);
                return null;
            }
        },
        update {
            @Override
            <T> T execute(T t, EntityManager em) throws Exception {
                em.merge(t);
                return t;
            }
        },
        delete {
            @Override
            <T> T execute(T t, EntityManager em) throws Exception {
                em.remove(em.merge(t));
                return null;
            }
        },
        refresh {
            @Override
            <T> T execute(T t, EntityManager entityManager) throws Exception {
                entityManager.refresh(entityManager.merge(t));
                return t;
            }
        };

        abstract <T> T execute(T t, EntityManager entityManager) throws Exception;
    }

    private Object object;
    private Type type;

    protected Change(Object obj, Type type) {
        this.object = obj;
        this.type = type;
    }

    void executeChange(EntityManager entityManager) throws Exception {
        if (object == null) return;
        type.execute(object, entityManager);
    }
}