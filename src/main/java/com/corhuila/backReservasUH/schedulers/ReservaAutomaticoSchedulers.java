package com.corhuila.backReservasUH.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.corhuila.backReservasUH.models.Constantes;
import com.corhuila.backReservasUH.models.Duracion;
import com.corhuila.backReservasUH.repositories.IDuracionRepository;
import com.corhuila.backReservasUH.repositories.IReservasRepository;
import com.corhuila.backReservasUH.services.DuracionServiceImpl;
import com.corhuila.backReservasUH.services.IDuracionService;

@Component
public class ReservaAutomaticoSchedulers {

    @Autowired
    private IDuracionService duracionService;

    @Autowired
    private IReservasRepository reservasRepository;

    @Autowired
    private IDuracionRepository duracionRepository;

    @Autowired
    private DuracionServiceImpl duracionServiceImpl;

    private static final int TIEMPO_MAXIMO_MINUTOS = Constantes.TIEMPO_MAXIMO_MINUTOS;

    @Scheduled(fixedRate = 1000) // Ejecutar cada segundo
    public void verificarReservas() {
        // Usar la hora de Colombia (America/Bogota) para toda la lógica del scheduler
        LocalDateTime ahora = LocalDateTime.now(java.time.ZoneId.of("America/Bogota"));
        String horaActual = ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[SCHEDULER] Verificando reservas a las: " + horaActual);

        reservasRepository.findAll().forEach(reserva -> {
            // Si la reserva está cancelada, desasociar la sala y no procesar más
            if ("Cancelada".equalsIgnoreCase(reserva.getEstado())) {
                if (reserva.getSalas() != null) {
                    reserva.getSalas().setEstado("Activa");
                    reservasRepository.save(reserva);
                }
                return;
            }

            LocalDate fechaReserva = reserva.getFecha();
            LocalTime horaReserva = reserva.getHora();
            if (fechaReserva == null || horaReserva == null) {
                System.out.println("[SCHEDULER] Reserva ID: " + reserva.getId() + " omitida por fecha u hora nula");
                return;
            }
            LocalDateTime inicioTeorico = LocalDateTime.of(fechaReserva, horaReserva);
            LocalDateTime finTeorico = inicioTeorico.plusMinutes(TIEMPO_MAXIMO_MINUTOS);

            boolean yaIniciado = duracionService.findByReservaId(reserva.getId()).isPresent();
            boolean yaFinalizado = yaIniciado
                    && duracionService.findByReservaId(reserva.getId()).get().getFinServicio() != null;

            // Establecer estado como "Reservado" si aún no ha iniciado
            if (!yaIniciado && ahora.isBefore(inicioTeorico)) {
                if (!"Reservada".equals(reserva.getEstado())) {
                    System.out
                            .println("[SCHEDULER] Cambiando estado a 'Reservado' para reserva ID: " + reserva.getId());
                    reserva.actualizarEstado("Reservada");
                    reservasRepository.save(reserva);

                    // Actualizar el estado en la entidad Duracion
                    Duracion duracion = duracionRepository.findByReservasId(reserva.getId()).orElse(new Duracion());
                    duracion.setReservas(reserva);
                    duracion.setEstado("Reservada");
                    duracionRepository.save(duracion);
                }
            }

            // Establecer estado como "En uso" si ya ha iniciado pero no ha finalizado
            if (yaIniciado && !yaFinalizado && ahora.isAfter(inicioTeorico) && ahora.isBefore(finTeorico)) {
                if (!"En uso".equals(reserva.getEstado())) {
                    System.out
                            .println("[SCHEDULER] Cambiando estado a 'En uso' para reserva ID: " + reserva.getId());
                    reserva.actualizarEstado("En uso");
                    reservasRepository.save(reserva);

                    // Actualizar el estado en la entidad Duracion
                    duracionService.findByReservaId(reserva.getId()).ifPresent(duracion -> {
                        duracion.setEstado("En uso");
                        duracionRepository.save(duracion);
                        // Enviar correo solo cuando cambia a 'En uso'
                        duracionServiceImpl.enviarCorreoInicioReserva(reserva, duracion);
                    });
                }
            }

            // Iniciar el servicio solo cuando la fecha y hora actuales coincidan o hayan
            // pasado
            if (!yaIniciado && !ahora.isBefore(inicioTeorico)) {
                duracionService.iniciarServicio(reserva.getId());
                System.out
                        .println("[SCHEDULER] Servicio INICIADO automáticamente para reserva ID: " + reserva.getId());
            }

            // Finalizar el servicio automáticamente solo si se ha superado el tiempo máximo
            if (yaIniciado && !yaFinalizado && ahora.isAfter(finTeorico)) {
                duracionService.finalizarServicio(reserva.getId(), false); // Finalización automática
            }
        });
    }

}
