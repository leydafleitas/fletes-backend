package org.fletes.rest;

import org.fletes.model.Cliente;
import org.fletes.service.ClienteService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @POST
    public Response crear(Cliente cliente) {
        if (cliente == null
                || cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getApellido() == null || cliente.getApellido().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()
                || cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos del cliente inválidos")
                    .build();
        }

        clienteService.create(cliente);

        return Response.created(URI.create("/clientes/" + cliente.getId()))
                .entity(cliente)
                .build();
    }

    @GET
    public Response listar() {
        List<Cliente> lista = clienteService.findAll();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Cliente cliente = clienteService.findById(id);

        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        return Response.ok(cliente).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Long id, Cliente cliente) {
        Cliente existente = clienteService.findById(id);

        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cliente no encontrado")
                    .build();
        }

        if (cliente == null
                || cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getApellido() == null || cliente.getApellido().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()
                || cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos del cliente inválidos")
                    .build();
        }

        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setEmail(cliente.getEmail());
        existente.setTelefono(cliente.getTelefono());

        clienteService.update(existente);

        return Response.ok(existente).build();
    }
}
