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
        entityManager.persist(camion);
    }

    public void update(Camion camion) {
        entityManager.merge(camion);
    }

    public void delete(Long id) {
        Camion camion = entityManager.find(Camion.class, id);
        if (camion != null) {
            camion.setActivo(false);
            entityManager.merge(camion);
        }
    }
}