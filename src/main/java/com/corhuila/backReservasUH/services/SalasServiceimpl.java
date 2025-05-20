package com.corhuila.backReservasUH.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Salas;
import com.corhuila.backReservasUH.models.Reservas;
import com.corhuila.backReservasUH.repositories.ISalasRepository;
import com.corhuila.backReservasUH.repositories.IReservasRepository;

@Service
public class SalasServiceimpl implements ISalasService {

    @Autowired
    private ISalasRepository salasRepository;

    @Autowired
    private IReservasRepository reservasRepository;

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
        // Eliminar todas las reservas asociadas a la sala antes de eliminar la sala
        List<Reservas> reservas = new java.util.ArrayList<>();
        reservasRepository.findAll().forEach(reserva -> {
            if (reserva.getSalas() != null && reserva.getSalas().getId().equals(id)) {
                reservas.add(reserva);
            }
        });
        for (Reservas reserva : reservas) {
            reservasRepository.deleteById(reserva.getId());
        }
        salasRepository.deleteById(id);
    }

}
