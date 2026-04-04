package org.fletes.rest;

import org.fletes.model.Camion;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
    EntityManager em;

    @POST
    @Transactional
    public Response crear(Camion camion) {
        if (camion == null || camion.getPatente() == null || camion.getPatente().isBlank()
                || camion.getMarca() == null || camion.getMarca().isBlank()
                || camion.getModelo() == null || camion.getModelo().isBlank()
                || camion.getCapacidadVolumen() == null || camion.getCapacidadVolumen() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos del camión inválidos")
                    .build();
        }

        Long cantidad = em.createQuery(
                "SELECT COUNT(c) FROM Camion c WHERE c.patente = :patente", Long.class)
                .setParameter("patente", camion.getPatente())
                .getSingleResult();

        if (cantidad > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Ya existe un camión con esa patente")
                    .build();
        }

        if (camion.getActivo() == null) {
            camion.setActivo(true);
        }

        em.persist(camion);

        return Response.created(URI.create("/camiones/" + camion.getId()))
                .entity(camion)
                .build();
    }

    @GET
    public Response listar() {
        List<Camion> lista = em.createQuery(
                "SELECT c FROM Camion c ORDER BY c.id", Camion.class)
                .getResultList();

        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Camion camion = em.find(Camion.class, id);

        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        return Response.ok(camion).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response modificar(@PathParam("id") Long id, Camion datos) {
        Camion camion = em.find(Camion.class, id);

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

        return Response.ok(camion).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Camion camion = em.find(Camion.class, id);

        if (camion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Camión no encontrado")
                    .build();
        }

        em.remove(camion);
        return Response.noContent().build();
    }
}