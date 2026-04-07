package org.fletes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.fletes.model.Cliente;
import java.util.List;

@ApplicationScoped
@Transactional
public class ClienteService {

    @Inject
    EntityManager em;

    public List<Cliente> findAll() {
        return em.createQuery("SELECT c FROM Cliente c ORDER BY c.id", Cliente.class).getResultList();
    }

    public Cliente findById(Long id) {
        return em.find(Cliente.class, id);
    }

    public void create(Cliente cliente) {
        if (findByEmail(cliente.getEmail()) != null) {
            throw new IllegalStateException("Ya existe un cliente con ese email: " + cliente.getEmail());
        }
        em.persist(cliente);
    }

    public Cliente findByEmail(String email) {
        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c WHERE c.email = :email", Cliente.class)
                .setParameter("email", email)
                .getResultList();
        return clientes.isEmpty() ? null : clientes.get(0);
    }
}
