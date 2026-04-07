package org.fletes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.fletes.model.Camion;
import org.fletes.model.Cliente;
import org.fletes.model.Reserva;
import org.fletes.model.Reserva.EstadoReserva;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Transactional
public class ReservaService {

    @Inject
    EntityManager em;

    // ─────────────────────────────────────────────
    // CREAR RESERVA
    // ─────────────────────────────────────────────

    public Reserva crearReserva(Long camionId, Long clienteId, String origen, String destino,
                                LocalDate fechaInicio, LocalDate fechaFin, Double volumenCarga) {

        if (origen == null || origen.isBlank())
            throw new IllegalArgumentException("El origen no puede estar vacío.");
        if (destino == null || destino.isBlank())
            throw new IllegalArgumentException("El destino no puede estar vacío.");
        if (fechaInicio == null || fechaFin == null)
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        if (fechaFin.isBefore(fechaInicio))
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        if (fechaInicio.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado.");
        if (volumenCarga == null || volumenCarga <= 0)
            throw new IllegalArgumentException("El volumen de carga debe ser mayor a 0.");

        Camion camion = em.find(Camion.class, camionId);
        if (camion == null)
            throw new IllegalArgumentException("No existe un camión con id: " + camionId);
        if (!camion.getActivo())
            throw new IllegalStateException("El camión con id " + camionId + " no está activo.");

        Cliente cliente = em.find(Cliente.class, clienteId);
        if (cliente == null)
            throw new IllegalArgumentException("No existe un cliente con id: " + clienteId);

        if (volumenCarga > camion.getCapacidadVolumen())
            throw new IllegalStateException(
                "El volumen solicitado (" + volumenCarga + " m³) supera la capacidad del camión ("
                + camion.getCapacidadVolumen() + " m³)."
            );

        if (!camionDisponible(camionId, fechaInicio, fechaFin, null))
            throw new IllegalStateException("El camión no está disponible en las fechas indicadas.");

        Reserva reserva = new Reserva();
        reserva.setCamion(camion);
        reserva.setCliente(cliente);
        reserva.setOrigen(origen);
        reserva.setDestino(destino);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setVolumenCarga(volumenCarga);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        em.persist(reserva);
        return reserva;
    }

    // ─────────────────────────────────────────────
    // CANCELAR RESERVA
    // ─────────────────────────────────────────────

    public Reserva cancelarReserva(Long reservaId) {
        Reserva reserva = em.find(Reserva.class, reservaId);

        if (reserva == null)
            throw new IllegalArgumentException("No existe una reserva con id: " + reservaId);
        if (reserva.getEstado() == EstadoReserva.CANCELADA)
            throw new IllegalStateException("La reserva ya se encuentra cancelada.");
        if (!LocalDate.now().isBefore(reserva.getFechaInicio()))
            throw new IllegalStateException("No se puede cancelar una reserva cuyo servicio ya comenzó.");

        reserva.setEstado(EstadoReserva.CANCELADA);
        return em.merge(reserva);
    }

    // ─────────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────────

    public List<Reserva> listarTodas() {
        return em.createQuery("SELECT r FROM Reserva r", Reserva.class).getResultList();
    }

    public Reserva buscarPorId(Long id) {
        Reserva reserva = em.find(Reserva.class, id);
        if (reserva == null)
            throw new IllegalArgumentException("No existe una reserva con id: " + id);
        return reserva;
    }

    public List<Reserva> listarPorCliente(Long clienteId) {
        return em.createQuery(
            "SELECT r FROM Reserva r WHERE r.cliente.id = :clienteId", Reserva.class)
            .setParameter("clienteId", clienteId)
            .getResultList();
    }

    public List<Reserva> listarPorCamion(Long camionId) {
        return em.createQuery(
            "SELECT r FROM Reserva r WHERE r.camion.id = :camionId", Reserva.class)
            .setParameter("camionId", camionId)
            .getResultList();
    }

    // ─────────────────────────────────────────────
    // DISPONIBILIDAD
    // ─────────────────────────────────────────────

    public List<Camion> buscarCamionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                   Double volumenRequerido) {
        if (fechaInicio == null || fechaFin == null)
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (!fechaFin.isAfter(fechaInicio))
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        if (volumenRequerido == null || volumenRequerido <= 0)
            throw new IllegalArgumentException("El volumen requerido debe ser mayor a 0.");

        return em.createQuery(
            "SELECT c FROM Camion c " +
            "WHERE c.activo = true " +
            "AND c.capacidadVolumen >= :volumen " +
            "AND c.id NOT IN (" +
            "   SELECT r.camion.id FROM Reserva r " +
            "   WHERE r.estado = org.fletes.model.Reserva$EstadoReserva.CONFIRMADA " +
            "   AND r.fechaInicio <= :fechaFin " +
            "   AND r.fechaFin >= :fechaInicio" +
            ")", Camion.class)
            .setParameter("volumen", volumenRequerido)
            .setParameter("fechaInicio", fechaInicio)
            .setParameter("fechaFin", fechaFin)
            .getResultList();
    }

    // ─────────────────────────────────────────────
    // MÉTODO INTERNO
    // ─────────────────────────────────────────────

    private boolean camionDisponible(Long camionId, LocalDate fechaInicio, LocalDate fechaFin,
                                     Long excludeReservaId) {
        String jpql = "SELECT COUNT(r) FROM Reserva r " +
                      "WHERE r.camion.id = :camionId " +
                      "AND r.estado = org.fletes.model.Reserva$EstadoReserva.CONFIRMADA " +
                      "AND r.fechaInicio <= :fechaFin " +
                      "AND r.fechaFin >= :fechaInicio";

        if (excludeReservaId != null)
            jpql += " AND r.id <> :excludeId";

        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
            .setParameter("camionId", camionId)
            .setParameter("fechaInicio", fechaInicio)
            .setParameter("fechaFin", fechaFin);

        if (excludeReservaId != null)
            query.setParameter("excludeId", excludeReservaId);

        return query.getSingleResult() == 0;
    }
}