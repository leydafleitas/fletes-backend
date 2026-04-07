package org.fletes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservas_id", nullable = false)
    private Reserva List<Reserva> reservas;

    @Column(nullable = false)
    private Long TotalDias;

    @Column(nullable = false)
    private Long monto;

    @Column(nullable = false)
    private Long totalDetalle;

    @Column(nullable = false)
    private LocalDate fechaActual;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Reserva List<Reserva> getReservas() { return reservas; }
    public void setId(Reserva List<Reserva>) { this.reservas = reservas; }

    public Long getTotalDias() { return TotalDias; }
    public void setTotalDias(Long TotalDias) { this.TotalDias = TotalDias; }

    public Long getMonto() { return monto; }
    public void setMonto(Long monto) { this.monto = monto; }

    public Long getTotalDetalle() { return totalDetalle; }
    public void setTotalDetalle(Long totalDetalle) { this.totalDetalle = totalDetalle; }

    public LocalDate getFechaActual() { return fechaActual; }
    public void setFechaActual(LocalDate fechaActual) { this.fechaActual = fechaActual; }
}
