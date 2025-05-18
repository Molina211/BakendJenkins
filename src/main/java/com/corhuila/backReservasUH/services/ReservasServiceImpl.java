package com.corhuila.backReservasUH.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Reservas;
import com.corhuila.backReservasUH.repositories.IReservasRepository;

@Service
public class ReservasServiceImpl implements IReservasService {

    @Autowired
    private IReservasRepository repository;


    @Transactional(readOnly = true)
    public List<Reservas> findAll() {
        return (List<Reservas>) repository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Reservas> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void update(Reservas reserva, Long id) {
        Optional<Reservas> reservaActual = repository.findById(id);
        if (reservaActual.isPresent()) {
            Reservas res = reservaActual.get();
            res.setSede(reserva.getSede());
            res.setMotivo(reserva.getMotivo());;
            res.setFecha(reserva.getFecha());
            res.setEstado(reserva.getEstado()); 
            repository.save(res);
        } else {
            System.out.println("Reserva no encontrada");
        }
    }

     @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);


    }

     @Override
    public List<Reservas> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

     @Override
    public void actualizarEstadoReserva(Long reservaId, String nuevoEstado) {
        Optional<Reservas> reservaOpt = repository.findById(reservaId);
        if (reservaOpt.isPresent()) {
            Reservas reserva = reservaOpt.get();
            reserva.actualizarEstado(nuevoEstado);
            repository.save(reserva);
        } else {
            throw new RuntimeException("Reserva no encontrada");
        }
    }

    @Override
@Transactional
public Reservas save(Reservas reserva) {
    if (!reserva.isEstadoValido()) {
        throw new IllegalArgumentException("Estado inválido: " + reserva.getEstado());
    }

    return repository.save(reserva);
}




}
