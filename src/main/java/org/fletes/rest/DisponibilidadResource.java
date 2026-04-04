package org.fletes.rest;

import org.fletes.model.Camion;
import org.fletes.model.Reserva;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/camiones/disponibles")
@Produces(MediaType.APPLICATION_JSON)
public class DisponibilidadResource {

    @Inject
    EntityManager em;

    @GET
    public Response buscarDisponibles(@QueryParam("fechaInicio") String fechaInicioStr,
                                      @QueryParam("fechaFin") String fechaFinStr,
                                      @QueryParam("volumen") Double volumen) {

        if (fechaInicioStr == null || fechaFinStr == null || volumen == null || volumen <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe enviar fechaInicio, fechaFin y volumen válidos")
                    .build();
        }

        LocalDate fechaInicio;
        LocalDate fechaFin;

        try {
            fechaInicio = LocalDate.parse(fechaInicioStr);
            fechaFin = LocalDate.parse(fechaFinStr);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Formato de fecha inválido. Use yyyy-MM-dd")
                    .build();
        }

        if (fechaFin.isBefore(fechaInicio)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La fecha fin no puede ser menor que la fecha inicio")
                    .build();
        }

        List<Camion> disponibles = em.createQuery("""
                SELECT c
                FROM Camion c
                WHERE c.activo = true
                  AND c.capacidadVolumen >= :volumen
                  AND c.id NOT IN (
                      SELECT r.camion.id
                      FROM Reserva r
                      WHERE r.estado <> :cancelada
                        AND r.fechaInicio <= :fechaFin
                        AND r.fechaFin >= :fechaInicio
                  )
                ORDER BY c.id
                """, Camion.class)
                .setParameter("volumen", volumen)
                .setParameter("cancelada", Reserva.EstadoReserva.CANCELADA)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();

        return Response.ok(disponibles).build();
    }
}