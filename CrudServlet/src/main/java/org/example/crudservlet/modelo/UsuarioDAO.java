package org.example.crudservlet.modelo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UsuarioDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public UsuarioDAO() {
        emf = Persistence.createEntityManagerFactory("biblioteca");
        em = emf.createEntityManager();
    }

    public boolean add(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public Usuario getById(Integer id) {
        return em.find(Usuario.class, id);
    }

    public Usuario getByDni(String dni) {
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.dni = :dni", Usuario.class);
        query.setParameter("dni", dni);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Usuario> getAll() {
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
        return query.getResultList();
    }

    public boolean update(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(usuario) ? usuario : em.merge(usuario));
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        em.close();
        emf.close();
    }
}
