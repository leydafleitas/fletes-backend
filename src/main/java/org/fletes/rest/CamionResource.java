package org.fletes.rest;

import org.fletes.model.Camion;
import org.fletes.service.CamionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/camiones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CamionResource {

    @Inject
    CamionService camionService;

    @POST
    public Response crear(Camion camion) {
        if (camion == null || camion.getPatente() == null || camion.getPatente().isBlank()
                || camion.getMarca() == null || camion.getMarca().isBlank()
                || camion.getModelo() == null || camion.getModelo().isBlank()
                || camion.getCapacidadVolumen() == null || camion.getCapacidadVolumen() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos del camión inválidos")
                    .build();
        }

        if (camion.getActivo() == null) {
            camion.setActivo(true);
        }

        camionService.create(camion);

        return Response.created(URI.create("/camiones/" + camion.getId()))
                .entity(camion)
                .build();
    }

    @GET
    public Response listar() {
        List<Camion> lista = camionService.findAll();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Camion camion = camionService.findById(id);

        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        return Response.ok(camion).build();
    }

    @PUT
    @Path("/{id}")
    public Response modificar(@PathParam("id") Long id, Camion datos) {
        Camion camion = camionService.findById(id);

        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        if (datos == null || datos.getPatente() == null || datos.getPatente().isBlank()
                || datos.getMarca() == null || datos.getMarca().isBlank()
                || datos.getModelo() == null || datos.getModelo().isBlank()
                || datos.getCapacidadVolumen() == null || datos.getCapacidadVolumen() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos del camión inválidos")
                    .build();
        }

        camion.setPatente(datos.getPatente());
        camion.setMarca(datos.getMarca());
        camion.setModelo(datos.getModelo());
        camion.setCapacidadVolumen(datos.getCapacidadVolumen());
        camion.setActivo(datos.getActivo() != null ? datos.getActivo() : true);

        camionService.update(camion);

        return Response.ok(camion).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        Camion camion = camionService.findById(id);

        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        camionService.delete(id);
        return Response.noContent().build();
    }
}
