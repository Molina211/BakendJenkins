package com.corhuila.backReservasUH.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Salas;
import com.corhuila.backReservasUH.repositories.ISalasRepository;

@Service
public class SalasServiceimpl implements ISalasService {

    @Autowired
    private ISalasRepository salasRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Salas> findAll() {
        return (List<Salas>) salasRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Salas findById(Long id) {
        return salasRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Salas save(Salas salas) {
        // Si el estado no está definido, se asigna "Activa" por defecto
        if (salas.getEstado() == null || salas.getEstado().isEmpty()) {
            salas.setEstado("Activa");
        }
        return salasRepository.save(salas);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        salasRepository.deleteById(id);
    }

}
