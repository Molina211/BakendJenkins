package com.corhuila.backReservasUH.Controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.corhuila.backReservasUH.models.Reservas;
import com.corhuila.backReservasUH.services.IReservasService;
import com.corhuila.backReservasUH.repositories.ISalasRepository;
import com.corhuila.backReservasUH.models.Salas;
import com.corhuila.backReservasUH.repositories.IReservasRepository;
import java.util.Arrays;

@CrossOrigin(origins = { "https://localhost" })
@RestController
@RequestMapping("/api")
public class ReservaRestController {

    @Autowired
    private IReservasService reservaService;

    @Autowired
    private ISalasRepository salasRepository;

    @Autowired
    private IReservasRepository reservasRepository;

    @GetMapping("/reservas")
    public List<Reservas> listarReservas() {
        return reservaService.findAll();
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        Optional<Reservas> reserva = reservaService.findById(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> registrarReserva(@RequestBody Reservas reserva) {
        try {
            // Si la reserva tiene una sala asociada, buscar la sala completa y actualizar
            // su estado
            if (reserva.getSalas() != null && reserva.getSalas().getId() != null) {
                Salas sala = salasRepository.findById(reserva.getSalas().getId()).orElse(null);
                if (sala == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sala no encontrada");
                }
                // Verificar si la sala ya está asociada a una reserva activa
                var reservasActivas = reservasRepository.findBySalasIdAndEstadoIn(
                        sala.getId(), Arrays.asList("Reservada", "En uso", "Ocupada"));
                if (!reservasActivas.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("La sala ya está reservada o en uso");
                }
                // Cambiar el estado de la sala a "Ocupada" si se reserva
                sala.setEstado("Ocupada");
                salasRepository.save(sala);
                reserva.setSalas(sala);
            }
            reserva.setId(null);
            Reservas nuevaReserva = reservaService.save(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la reserva: " + e.getMessage());
        }
    }

    @PutMapping("/reservas/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> actualizarReserva(@RequestBody Reservas reserva, @PathVariable Long id) {
        Optional<Reservas> reservaActual = reservaService.findById(id);
        if (reservaActual.isPresent()) {
            reservaService.update(reserva, id);
            return ResponseEntity.ok("Reserva actualizada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }
    }

    @DeleteMapping("/reservas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        Optional<Reservas> reserva = reservaService.findById(id);
        if (reserva.isPresent()) {
            reservaService.delete(id);
            return ResponseEntity.ok("Reserva eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }
    }

    @GetMapping("/reservas/estado/{estado}")
    public ResponseEntity<List<Reservas>> getReservasByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservaService.findByEstado(estado));
    }

    @PutMapping("/reservas/{id}/estado")
    public ResponseEntity<?> actualizarEstadoReserva(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("nuevoEstado");
            reservaService.actualizarEstadoReserva(id, nuevoEstado);
            return ResponseEntity.ok("Estado de la reserva actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
