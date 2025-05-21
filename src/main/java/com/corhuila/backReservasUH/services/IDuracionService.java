package com.corhuila.backReservasUH.services;

import java.util.List;
import java.util.Optional;

import com.corhuila.backReservasUH.models.Duracion;

public interface IDuracionService {

    public Duracion iniciarServicio(Long reservaId);

    public Duracion finalizarServicio(Long reservaId);

    public Duracion finalizarServicio(Long reservaId, boolean esManual);

    public Optional<Duracion> findByReservaId(Long reservaId);

    public List<Duracion> findAllDuraciones();

}
