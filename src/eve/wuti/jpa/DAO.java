package eve.wuti.jpa;

import eve.wuti.http.WebXML;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.sql.Connection;
import java.util.*;

/**
 * User: jrubio
 * Date: 29-may-2013
 */
public class DAO {
    public static TreeMap<String, DAO> existingDAOs = new TreeMap<>() ;
    private EntityManager entityManager;
    private Connection connection;


    public static DAO get() {
        return get(WebXML.<String>readParameter("JPA_persistenceUnit"));
    }

    public static DAO get(String persistenceUnit) {
        DAO dao = null;

        if (persistenceUnit == null) persistenceUnit = "COE";

        if (existingDAOs.containsKey(persistenceUnit))
            dao = existingDAOs.get(persistenceUnit);

        if (dao == null || !dao.isValid()) {
            dao = new DAO(persistenceUnit);
            existingDAOs.put(persistenceUnit, dao);
        }

        return dao;
    }

    private DAO(String persistenceUnit) {
        entityManager = Persistence.createEntityManagerFactory(persistenceUnit).createEntityManager();
    }

    public Connection getCt() {
        entityManager.unwrap(Session.class).doWork(con -> connection = con);
        return connection;
    }

    private boolean isValid() {
//        return entityManager.unwrap(Session.class).isConnected();
        return true;
    }

    public <T> T getObject(Class<T> t, Long id) {
        return entityManager.find(t, id);
    }

    public <T> T getObject(Class<T> tClass, String namedQuery, String param, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(param, value);
        return getObject(tClass, namedQuery, map);
    }

    public <T> T getObject(Class<T> tClass, String namedQuery, String param1, Object value1, String param2, Object value2) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(param1, value1);
        map.put(param2, value2);
        return getObject(tClass, namedQuery, map);
    }

    public <T> T getObject(Class<T> tClass, String namedQuery, Map<String, Object> parameters) {

        TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, tClass);
        if (parameters != null) for (String key : parameters.keySet()) query.setParameter(key, parameters.get(key));

        List<T> results = query.setMaxResults(1).getResultList();

        if (results != null && results.size() > 1)
            System.out.println("Watch out: more than one result for " + namedQuery); // Alternative: throw new NonUniqueResultException();

        if (results != null && results.size() == 1)
            return results.get(0);
        else
            return null;
    }

    public <T> List<T> getList(Class<T> aClass) {
        return entityManager.createQuery("SELECT e FROM " + aClass.getName() + " e", aClass).getResultList();
    }

    public <T> List<T> getList(Class<T> tClass, String namedQuery, String param, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(param, value);
        return getList(tClass, namedQuery, map);
    }

    public <T> List<T> getList(Class<T> tClass, String namedQuery, String param1, Object value1, String param2, Object value2) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(param1, value1);
        map.put(param2, value2);
        return getList(tClass, namedQuery, map);
    }

    public <T> List<T> getList(Class<T> tClass, String namedQuery, Map<String, Object> parameters) {
        return getList(tClass, namedQuery, parameters, null) ;
    }

    public <T> List<T> getList(Class<T> tClass, String namedQuery, Map<String, Object> parameters, Integer maxNrRowsReturned) {
        TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, tClass);
        if (parameters != null) for (String key : parameters.keySet()) query.setParameter(key, parameters.get(key));
        if (maxNrRowsReturned != null) query.setMaxResults(maxNrRowsReturned) ;
        return query.getResultList();
    }

    public void refresh(Object obj) {
        entityManager.refresh(obj);
    }

    public synchronized void execute(List<Change> changes) throws Exception {
        EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();
            for (Change change : changes)
                change.executeChange(entityManager);
            tx.commit();
        } catch (Throwable th) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Error updating the database: " + th.getMessage() + " - " + th.getCause());
        }
    }

    public synchronized void execute(List<Change> changes, int inBlocksOfSize) throws Exception {
        int ptoIni = 0;
        while (true) {
            if (ptoIni >= changes.size()) break;
            int ptoFin = ptoIni + inBlocksOfSize;
            if (ptoFin > changes.size()) ptoFin = changes.size();
            List<Change> acts = changes.subList(ptoIni, ptoFin);
            execute(acts);
            ptoIni = ptoFin;
        }
    }


    public String sanitize(String s) {
        return (s == null || s.trim().length() == 0) ? s : s.replace("'", "").replace("\\", "").replace("{", "").replace("}", "");
    }

    public synchronized void execute(Change... changes) throws Exception {
        if (changes != null && changes.length > 0)
            execute(Arrays.asList(changes));
    }

    public synchronized <T> List<T> executeTyped(String query, Class<T> classT) throws Exception {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            return entityManager.createQuery(query, classT).getResultList();
        } catch (Throwable th) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Errors executing the query: " + th.getMessage());
        }
    }

    public synchronized void executeUpdateOrDeleteQuery(String updateOrDeleteQuery) throws Exception {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.createQuery(updateOrDeleteQuery).executeUpdate();
            tx.commit();
        } catch (Throwable th) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Errors executing the query: " + th.getMessage());
        }
    }


}