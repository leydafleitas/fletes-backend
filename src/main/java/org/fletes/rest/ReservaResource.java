package org.fletes.rest;

import org.fletes.dto.ReservaRequest;
import org.fletes.model.Camion;
import org.fletes.model.Cliente;
import org.fletes.model.Reserva;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaResource {

    @Inject
    EntityManager em;

    @POST
    @Transactional
    public Response crear(ReservaRequest req) {
        if (req == null
                || req.camionId == null
                || req.clienteId == null
                || req.origen == null || req.origen.isBlank()
                || req.destino == null || req.destino.isBlank()
                || req.fechaInicio == null
                || req.fechaFin == null
                || req.volumenCarga == null || req.volumenCarga <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos de la reserva inválidos")
                    .build();
        }

        if (req.fechaFin.isBefore(req.fechaInicio)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La fecha fin no puede ser menor que la fecha inicio")
                    .build();
        }

        Camion camion = em.find(Camion.class, req.camionId);
        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        Cliente cliente = em.find(Cliente.class, req.clienteId);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        if (!Boolean.TRUE.equals(camion.getActivo())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El camión no está activo")
                    .build();
        }

        if (req.volumenCarga > camion.getCapacidadVolumen()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El volumen de carga supera la capacidad del camión")
                    .build();
        }

        Long conflictos = em.createQuery("""
                SELECT COUNT(r)
                FROM Reserva r
                WHERE r.camion.id = :camionId
                  AND r.estado <> :cancelada
                  AND r.fechaInicio <= :fechaFin
                  AND r.fechaFin >= :fechaInicio
                """, Long.class)
                .setParameter("camionId", req.camionId)
                .setParameter("cancelada", Reserva.EstadoReserva.CANCELADA)
                .setParameter("fechaInicio", req.fechaInicio)
                .setParameter("fechaFin", req.fechaFin)
                .getSingleResult();

        if (conflictos > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El camión ya tiene una reserva en ese rango de fechas")
                    .build();
        }

        Reserva reserva = new Reserva();
        reserva.setCamion(camion);
        reserva.setCliente(cliente);
        reserva.setOrigen(req.origen);
        reserva.setDestino(req.destino);
        reserva.setFechaInicio(req.fechaInicio);
        reserva.setFechaFin(req.fechaFin);
        reserva.setVolumenCarga(req.volumenCarga);
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);

        em.persist(reserva);

        return Response.created(URI.create("/reservas/" + reserva.getId()))
                .entity(reserva)
                .build();
    }

    @GET
    public Response listar() {
        List<Reserva> lista = em.createQuery(
                "SELECT r FROM Reserva r ORDER BY r.id", Reserva.class)
                .getResultList();

        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Reserva reserva = em.find(Reserva.class, id);

        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reserva no encontrada")
                    .build();
        }

        return Response.ok(reserva).build();
    }

    @PUT
    @Path("/{id}/cancelar")
    @Transactional
    public Response cancelar(@PathParam("id") Long id) {
        Reserva reserva = em.find(Reserva.class, id);

        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reserva no encontrada")
                    .build();
        }

        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("La reserva ya está cancelada")
                    .build();
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        return Response.ok(reserva).build();
    }
}