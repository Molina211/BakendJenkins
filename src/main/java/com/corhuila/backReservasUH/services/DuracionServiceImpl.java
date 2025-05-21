package com.corhuila.backReservasUH.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.List;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
                String fechaHoraFormateada = duracion.getInicioServicio().format(formatter);
                String subject = "Inicio de Reserva";
                String htmlMessage = "<html>"
                        + "<body style=\"font-family: Arial, sans-serif; background: #ffffff; margin:0; padding:0;\">"
                        + "<div style=\"background: #000000;\">"
                        + "<svg viewBox='0 0 1440 150' preserveAspectRatio='none' xmlns='http://www.w3.org/2000/svg' style='display:block;'><style>.animated-path{animation:waveAnim 10s linear infinite;}@keyframes waveAnim{0%{d:path('M0,75 C480,0 960,150 1440,75 L1440,0 L0,0 Z');}20%{d:path('M0,75 C400,100 1040,0 1440,75 L1440,0 L0,0 Z');}40%{d:path('M0,75 C500,150 940,0 1440,75 L1440,0 L0,0 Z');}60%{d:path('M0,75 C580,0 860,150 1440,75 L1440,0 L0,0 Z');}80%{d:path('M0,75 C200,0 960,150 1440,75 L1440,0 L0,0 Z');}100%{d:path('M0,75 C480,0 960,150 1440,75 L1440,0 L0,0 Z');}}</style><path d='M0,75 C480,0 960,150 1440,75 L1440,0 L0,0 Z' fill='#01963f' class='animated-path'/></svg>"
                        + "</div>"
                        + "<div style=\"padding: 32px 16px 16px 16px; background: #ffffff; border-radius: 0 0 12px 12px; box-shadow: 0 2px 8px rgba(1,150,63,0.08); max-width: 480px; margin: 0 auto;\">"
                        + "<h2 style=\"color: #01963f;\">¡Tu reserva ha iniciado!</h2>"
                        + "<p style=\"font-size: 16px; color: #000000;\">La reserva con ID <b>" + reserva.getId()
                        + "</b> ha iniciado su tiempo a las <b>" + fechaHoraFormateada + "</b>.</p>"
                        + "<div style=\"background: #01963f; color: #ffffff; padding: 20px; border-radius: 8px; margin: 24px 0; text-align: center; font-size: 20px; font-weight: bold; letter-spacing: 2px;\">"
                        + "Reserva ID: " + reserva.getId()
                        + "</div>"
                        + "</div>"
                        + "<div style=\"background: #000000;\">"
                        + "<svg viewBox='0 0 1440 150' preserveAspectRatio='none' xmlns='http://www.w3.org/2000/svg' style='display:block;'><style>.animated-path2{animation:waveBottomAnim 10s linear infinite;}@keyframes waveBottomAnim{0%{d:path('M0,75 C480,0 960,150 1440,75 L1440,150 L0,150 Z');}20%{d:path('M0,75 C400,100 1040,0 1440,75 L1440,150 L0,150 Z');}40%{d:path('M0,75 C500,150 940,0 1440,75 L1440,150 L0,150 Z');}60%{d:path('M0,75 C580,0 860,150 1440,75 L1440,150 L0,150 Z');}80%{d:path('M0,75 C200,0 960,150 1440,75 L1440,150 L0,150 Z');}100%{d:path('M0,75 C480,0 960,150 1440,75 L1440,150 L0,150 Z');}}</style><path d='M0,75 C480,0 960,150 1440,75 L1440,150 L0,150 Z' fill='#01963f' class='animated-path2'/></svg>"
                        + "</div>"
                        + "</body>"
                        + "</html>";
                jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
                org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                        mimeMessage, true, "UTF-8");
                helper.setTo(correoDestino);
                helper.setSubject(subject);
                helper.setText(htmlMessage, true);
                mailSender.send(mimeMessage);
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
        // o automática) o fue cancelada
        if (("Terminada".equalsIgnoreCase(reserva.getEstado()) || "Cancelada".equalsIgnoreCase(reserva.getEstado()))
                && reserva.getSalas() != null) {
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
