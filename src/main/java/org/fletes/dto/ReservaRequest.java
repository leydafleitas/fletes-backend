package org.fletes.dto;

import java.time.LocalDate;

public class ReservaRequest {
    public Long camionId;
    public Long clienteId;
    public String origen;
    public String destino;
    public LocalDate fechaInicio;
    public LocalDate fechaFin;
    public Double volumenCarga;
}