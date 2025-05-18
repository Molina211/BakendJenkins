package com.corhuila.backReservasUH.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.List;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.corhuila.backReservasUH.models.Duracion;
import com.corhuila.backReservasUH.models.Reservas;
import com.corhuila.backReservasUH.repositories.IDuracionRepository;
import com.corhuila.backReservasUH.repositories.IReservasRepository;
import com.corhuila.backReservasUH.repositories.ISalasRepository;
import com.corhuila.backReservasUH.models.Salas;

@Service
public class DuracionServiceImpl implements IDuracionService {

    @Autowired
    private IReservasRepository reservasRepository;

    @Autowired
    private IDuracionRepository duracionRepository;

    @Autowired
    private ISalasRepository salasRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Duracion iniciarServicio(Long reservaId) {
        Reservas reserva = reservasRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Duracion duracion = duracionRepository.findByReservasId(reservaId)
                .orElse(new Duracion());

        duracion.setReservas(reserva);

        // Usar la zona horaria de Colombia
        LocalDateTime inicioColombia = LocalDateTime.now(ZoneId.of("America/Bogota"));
        duracion.setInicioServicio(inicioColombia);

        // Si la reserva está en estado "Reservado" y no se ha iniciado automáticamente
        if ("Reservada".equals(reserva.getEstado())) {
            duracion.setEstado("Reservada");
        }

        // El correo se enviará solo desde el scheduler para evitar bucles
        return duracionRepository.save(duracion);
    }

    public void enviarCorreoInicioReserva(Reservas reserva, Duracion duracion) {
        try {
            String correoDestino = null;
            if (reserva.getUsuario() != null && reserva.getUsuario().getCorreo() != null) {
                correoDestino = reserva.getUsuario().getCorreo();
            }
            if (correoDestino != null) {
                // Formatear fecha y hora en formato de 12 horas y fecha normal
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
                String fechaHoraFormateada = duracion.getInicioServicio().format(formatter);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(correoDestino);
                message.setSubject("Inicio de Reserva");
                message.setText(
                        "La reserva con ID " + reserva.getId() + " ha iniciado su tiempo a las " + fechaHoraFormateada
                                + ".");
                mailSender.send(message);
            }
        } catch (Exception e) {
            System.out.println("No se pudo enviar el correo de notificación: " + e.getMessage());
        }
    }

    @Override
    public Duracion finalizarServicio(Long reservaId, boolean esManual) {
        Reservas reserva = reservasRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Duracion duracion = duracionRepository.findByReservasId(reservaId)
                .orElseThrow(() -> new RuntimeException("Duración no iniciada"));

        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));
        // Calcular y guardar la diferencia de tiempo en minutos si hay inicio y fin
        if (duracion.getInicioServicio() != null) {
            long segundos = java.time.Duration.between(duracion.getInicioServicio(), ahora).toSeconds();
            duracion.setTiempoSala(segundos);
        }

        if (!esManual) {
            duracion.setFinServicio(ahora);
        }

        if (esManual) {
            duracion.setFinServicio(ahora);
        } else if (duracion.getFinServicio() == null) {
            duracion.setFinServicio(ahora);
        }
        if (esManual) {
            reserva.actualizarEstado("Terminada");
            reservasRepository.save(reserva);
            duracion.setEstado("Terminada");
        }

        Duracion duracionFinalizada = duracionRepository.save(duracion);

        if (esManual) {
            System.out.println("[MANUAL] Servicio FINALIZADO manualmente para reserva ID: " + reservaId);
        } else {
            System.out.println("[SCHEDULER] Servicio FINALIZADO automáticamente para reserva ID: " + reservaId);
            reserva.actualizarEstado("Terminada");
            reservasRepository.save(reserva);
            duracion.setEstado("Terminada");
        }

        // Desasociar la sala y ponerla en estado 'Activa' si la reserva terminó (manual
        // o automática)
        if ("Terminada".equalsIgnoreCase(reserva.getEstado()) && reserva.getSalas() != null) {
            Salas sala = reserva.getSalas();
            sala.setEstado("Activa");
            salasRepository.save(sala);
            reserva.setSalas(null);
            reservasRepository.save(reserva);
        }
        // Cambiar el estado de la duración a 'Terminada' si la reserva terminó
        // automáticamente
        if ("Terminada".equalsIgnoreCase(reserva.getEstado())) {
            duracion.setEstado("Terminada");
            duracionRepository.save(duracion);
        }

        return duracionFinalizada;
    }

    @Override
    public Duracion finalizarServicio(Long reservaId) {
        return finalizarServicio(reservaId, true); // Por defecto, se asume que es manual
    }

    @Override
    public Optional<Duracion> findByReservaId(Long reservaId) {
        return duracionRepository.findByReservasId(reservaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Duracion> findAllDuraciones() {
        return duracionRepository.findAll();
    }
}
