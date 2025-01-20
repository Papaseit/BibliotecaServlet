package org.example.crudservlet.modelo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PrestamoDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public PrestamoDAO() {
        emf = Persistence.createEntityManagerFactory("biblioteca");
        em = emf.createEntityManager();
    }

    public boolean add(Prestamo prestamo) {
        try {
            em.getTransaction().begin();
            em.persist(prestamo);
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

    public Prestamo getById(int id) {
        return em.find(Prestamo.class, id);
    }

    public List<Prestamo> getAll() {
        TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p", Prestamo.class);
        return query.getResultList();
    }

    public boolean update(Prestamo prestamo) {
        try {
            em.getTransaction().begin();
            em.merge(prestamo);
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

    public boolean delete(Prestamo prestamo) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(prestamo) ? prestamo : em.merge(prestamo));
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
