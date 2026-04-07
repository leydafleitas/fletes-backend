package org.fletes.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String error = "Error interno del servidor";

        if (exception instanceof IllegalStateException) {
            status = Response.Status.CONFLICT;
            error = "Conflicto en la operación";
        } else if (exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST;
            error = "Datos inválidos";
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "error", error,
                        "detalle", exception.getMessage()
                ))
                .build();
    }
}
