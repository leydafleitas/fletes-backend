package org.fletes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "camiones")
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String patente;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Double capacidadVolumen; // en metros cúbicos (m³)

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Double getCapacidadVolumen() { return capacidadVolumen; }
    public void setCapacidadVolumen(Double capacidadVolumen) { this.capacidadVolumen = capacidadVolumen; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}