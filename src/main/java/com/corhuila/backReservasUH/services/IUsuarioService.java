package com.corhuila.backReservasUH.services;

import java.util.List;
import java.util.Optional;

import com.corhuila.backReservasUH.models.Usuario;

public interface IUsuarioService {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Usuario save(Usuario usuario);

    void delete(Long id);

    Optional<Usuario> login(String correo, String password);

    Usuario findByCorreo(String correo);

    String enviarCodigoVerificacion(String correo);

    boolean verificarCodigo(String correo, String codigo);

}
