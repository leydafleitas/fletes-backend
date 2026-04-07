package org.fletes.rest;

import org.fletes.model.Camion;
import org.fletes.service.ReservaService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/camiones/disponibles")
@Produces(MediaType.APPLICATION_JSON)
public class DisponibilidadResource {

    @Inject
    ReservaService reservaService;

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

        List<Camion> disponibles = reservaService.buscarCamionesDisponibles(fechaInicio, fechaFin, volumen);

        return Response.ok(disponibles).build();
    }
}
