package Dao;

import Model.User;
import data.DBUntil;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

public class UserDB {

    public static void insert(User user) {
        EntityManager em = DBUntil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(user);
            trans.commit();
        } catch (Exception e) {
            System.out.println("Error in UserDB.insert: " + e.getMessage());
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }

    public static boolean emailExists(String email) {
        EntityManager em = DBUntil.getEmFactory().createEntityManager();
        String qString = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        TypedQuery<Long> query = em.createQuery(qString, Long.class);
        query.setParameter("email", email);
        try {
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            System.out.println("Error in UserDB.emailExists: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}

