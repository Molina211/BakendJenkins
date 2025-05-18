package com.corhuila.backReservasUH.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corhuila.backReservasUH.models.Duracion;

public interface IDuracionRepository extends JpaRepository<Duracion, Long> {

    Optional<Duracion> findByReservasId(Long reservaId);

}
