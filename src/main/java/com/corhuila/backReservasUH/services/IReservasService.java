package com.corhuila.backReservasUH.services;

import java.util.List;
import java.util.Optional;

import com.corhuila.backReservasUH.models.Reservas;

public interface IReservasService {

    public List<Reservas> findAll();

    public Optional<Reservas> findById(Long id);

    public Reservas save(Reservas reserva);

    public void update(Reservas reserva, Long id);

    public void delete(Long id);

    List<Reservas> findByEstado(String estado);

    void actualizarEstadoReserva(Long reservaId, String nuevoEstado);

}
