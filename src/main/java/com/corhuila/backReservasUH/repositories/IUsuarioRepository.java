package com.corhuila.backReservasUH.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.corhuila.backReservasUH.models.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

}
