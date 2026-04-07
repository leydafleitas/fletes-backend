package org.fletes.rest;

import org.fletes.dto.ReservaRequest;
import org.fletes.model.Reserva;
import org.fletes.service.ReservaService;

import jakarta.inject.Inject;
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
    ReservaService reservaService;

    @POST
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

        Reserva reserva = reservaService.crearReserva(
                req.camionId,
                req.clienteId,
                req.origen,
                req.destino,
                req.fechaInicio,
                req.fechaFin,
                req.volumenCarga
        );

        return Response.created(URI.create("/reservas/" + reserva.getId()))
                .entity(reserva)
                .build();
    }

    @GET
    public Response listar() {
        List<Reserva> lista = reservaService.listarTodas();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return Response.ok(reserva).build();
    }

    @PUT
    @Path("/{id}/cancelar")
    public Response cancelar(@PathParam("id") Long id) {
        Reserva reserva = reservaService.cancelarReserva(id);
        return Response.ok(reserva).build();
    }
}
