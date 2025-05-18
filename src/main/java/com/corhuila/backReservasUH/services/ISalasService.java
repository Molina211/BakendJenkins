package com.corhuila.backReservasUH.services;

import java.util.List;

import com.corhuila.backReservasUH.models.Salas;

public interface ISalasService {

    public List<Salas> findAll();

    public Salas findById(Long id);

    public Salas save(Salas salas);

    public void delete(Long id);
}
