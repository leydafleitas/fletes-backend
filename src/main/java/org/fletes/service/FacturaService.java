package org.fletes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.fletes.model.Factura;
import java.util.List;

@ApplicationScoped
@Transactional
public class FacturaService {

    @Inject
    EntityManager em;

    public List<Factura> findAll() {
        return em.createQuery("SELECT f FROM Factura f ORDER BY f.id", Factura.class).getResultList();
    }

    public Factura findById(Long id) {
        return em.find(Factura.class, id);
    }

    public void create(Factura Factura) {
        em.persist(Factura);
    }

    public void update(Factura Factura) {
        Factura existing = findById(Factura.getId());
        if (existing != null && !existing.getId().equals(Factura.getId())) {
            throw new IllegalStateException("Ya existe otro Factura con ese email: " + Factura.getEmail());
        }
        em.merge(Factura);
    }

    public Factura findByEmail(String email) {
        List<Factura> Facturas = em.createQuery("SELECT c FROM Factura c WHERE c.email = :email", Factura.class)
                .setParameter("email", email)
                .getResultList();
        return Facturas.isEmpty() ? null : Facturas.get(0);
    }
}
