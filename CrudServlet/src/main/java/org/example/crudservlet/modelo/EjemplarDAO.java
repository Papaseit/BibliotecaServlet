package org.example.crudservlet.modelo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EjemplarDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public EjemplarDAO() {
        emf = Persistence.createEntityManagerFactory("biblioteca");
        em = emf.createEntityManager();
    }

    public boolean add(Ejemplar ejemplar) {
        try {
            em.getTransaction().begin();
            em.persist(ejemplar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public Ejemplar getById(int id) {
        return em.find(Ejemplar.class, id);
    }

    public List<Ejemplar> getAll() {
        TypedQuery<Ejemplar> query = em.createQuery("SELECT e FROM Ejemplar e", Ejemplar.class);
        return query.getResultList();
    }

    public boolean update(Ejemplar ejemplar) {
        try {
            em.getTransaction().begin();
            em.merge(ejemplar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Ejemplar ejemplar) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(ejemplar) ? ejemplar : em.merge(ejemplar));
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (em.isOpen()) {
            em.close();
        }
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
