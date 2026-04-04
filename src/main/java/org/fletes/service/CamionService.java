package org.fletes.service;

import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.fletes.model.Camion;
import java.util.List;

@Stateless
@Transactional
public class CamionService {

    public List<Camion> findAll() {
        return Camion.listAll();
    }

    public List<Camion> findActive() {
        return Camion.find("activo", true).list();
    }

    public Camion findById(Long id) {
        return Camion.findById(id);
    }

    public void create(Camion camion) {
        camion.persist();
    }

    public void update(Camion camion) {
        camion.persist();
    }

    public void delete(Long id) {
        Camion camion = Camion.findById(id);
        if (camion != null) {
            camion.activo = false;
            camion.persist();
        }
    }
}