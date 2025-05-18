package com.corhuila.backReservasUH.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "duraciones")
public class Duracion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "reserva_id", referencedColumnName = "id")
  private Reservas reservas;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "inicio_servicio")
  private LocalDateTime inicioServicio;

  @Column(name = "tiempo_sala")
  private Long tiempoSala;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "fin_servicio")
  private LocalDateTime finServicio;

  @Column(name = "estado")
  private String estado;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Reservas getReservas() {
    return reservas;
  }

  public void setReservas(Reservas reservas) {
    this.reservas = reservas;
  }

  public LocalDateTime getInicioServicio() {
    return inicioServicio;
  }

  public void setInicioServicio(LocalDateTime inicioServicio) {
    this.inicioServicio = inicioServicio;
  }

  public Long getTiempoSala() {
    return tiempoSala;
  }

  public void setTiempoSala(Long tiempoSala) {
    this.tiempoSala = tiempoSala;
  }

  public LocalDateTime getFinServicio() {
    return finServicio;
  }

  public void setFinServicio(LocalDateTime finServicio) {
    this.finServicio = finServicio;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

}
