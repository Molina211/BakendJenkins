package com.corhuila.backReservasUH.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.corhuila.backReservasUH.models.Reservas;

public interface IReservasRepository extends CrudRepository<Reservas, Long> {

    List<Reservas> findByEstado(String estado);

    List<Reservas> findBySalasIdAndEstadoIn(Long salaId, List<String> estados);

}
