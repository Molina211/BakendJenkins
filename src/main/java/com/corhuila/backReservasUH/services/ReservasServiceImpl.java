package com.corhuila.backReservasUH.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Reservas;
import com.corhuila.backReservasUH.repositories.IReservasRepository;
import com.corhuila.backReservasUH.repositories.ISalasRepository;

@Service
public class ReservasServiceImpl implements IReservasService {

    @Autowired
    private IReservasRepository repository;

    @Autowired
    private ISalasRepository salasRepository;

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
            res.setMotivo(reserva.getMotivo());
            res.setFecha(reserva.getFecha());
            res.setEstado(reserva.getEstado());
            // Update associated room (salas) if provided
            if (reserva.getSalas() != null) {
                res.setSalas(reserva.getSalas());
            }
            // Update associated user (usuario) if provided
            if (reserva.getUsuario() != null) {
                res.setUsuario(reserva.getUsuario());
            }
            repository.save(res);
        } else {
            System.out.println("Reserva no encontrada");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<Reservas> reservaOpt = repository.findById(id);
        if (reservaOpt.isPresent()) {
            Reservas reserva = reservaOpt.get();
            if (reserva.getSalas() != null) {
                // Desasociar la sala y ponerla en estado 'Activa'
                reserva.getSalas().setEstado("Activa");
                salasRepository.save(reserva.getSalas()); // Guardar el cambio en la sala
                reserva.setSalas(null);
            }
            repository.save(reserva); // Guardar la reserva desasociada
            repository.deleteById(id);
        }
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
            // Si se cancela, desasociar la sala y ponerla en 'Activa'
            if ("Cancelada".equalsIgnoreCase(nuevoEstado) && reserva.getSalas() != null) {
                reserva.getSalas().setEstado("Activa");
                salasRepository.save(reserva.getSalas());
                reserva.setSalas(null);
            }
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
