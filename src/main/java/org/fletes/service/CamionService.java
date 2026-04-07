package org.fletes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.fletes.model.Camion;
import java.util.List;

@ApplicationScoped
@Transactional
public class CamionService {

    @Inject
    EntityManager entityManager;

    public List<Camion> findAll() {
        return entityManager.createQuery("SELECT c FROM Camion c", Camion.class).getResultList();
    }

    public List<Camion> findActive() {
        return entityManager.createQuery("SELECT c FROM Camion c WHERE c.activo = true", Camion.class)
                .getResultList();
    }

    public Camion findById(Long id) {
        return entityManager.find(Camion.class, id);
    }

    public void create(Camion camion) {
        if (findByPatente(camion.getPatente()) != null) {
            throw new IllegalStateException("Ya existe un camión con la patente: " + camion.getPatente());
        }
        entityManager.persist(camion);
    }

    public void update(Camion camion) {
        Camion existing = findByPatente(camion.getPatente());
        if (existing != null && !existing.getId().equals(camion.getId())) {
            throw new IllegalStateException("Ya existe otro camión con la patente: " + camion.getPatente());
        }
        entityManager.merge(camion);
    }

    public Camion findByPatente(String patente) {
        List<Camion> camiones = entityManager.createQuery("SELECT c FROM Camion c WHERE c.patente = :patente", Camion.class)
                .setParameter("patente", patente)
                .getResultList();
        return camiones.isEmpty() ? null : camiones.get(0);
    }

    public void delete(Long id) {
        Camion camion = entityManager.find(Camion.class, id);
        if (camion != null) {
            camion.setActivo(false);
            entityManager.merge(camion);
        }
    }
}