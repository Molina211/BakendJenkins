package com.corhuila.backReservasUH.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Usuario;
import com.corhuila.backReservasUH.repositories.IUsuarioRepository;
import com.corhuila.backReservasUH.repositories.IReservasRepository;
import com.corhuila.backReservasUH.models.Reservas;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioServiceimpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IReservasRepository reservasRepository;

    private final Map<String, String> codigosPorCorreo = new HashMap<>();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().startsWith("$2a$")) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String email, String rawPassword) {
        Optional<Usuario> userOpt = usuarioRepository.findByCorreo(email);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getContrasena())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElse(null);

    }

    @Override
    public String enviarCodigoVerificacion(String correo) {
        // Solo verificar que el usuario exista, sin guardar variable
        usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar nuevo código
        String nuevoCodigo = generarCodigo();

        // Guardar el código en el mapa (correo -> código)
        codigosPorCorreo.put(correo, nuevoCodigo);

        // PLANTILLA HTML PERSONALIZADA SOLO SVG ESTÁTICO Y ESTILOS INLINE
        String subject = "Código de verificación de cuenta";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background: #ffffff; margin:0; padding:0;\">"
                + "<div style=\"background: #000000;\">"
                + "<svg viewBox='0 0 1440 150' preserveAspectRatio='none' xmlns='http://www.w3.org/2000/svg' style='display:block;'><path d='M0,75 C480,0 960,150 1440,75 L1440,0 L0,0 Z' fill='#01963f'/></svg>"
                + "</div>"
                + "<div style=\"padding: 32px 16px 16px 16px; background: #ffffff; border-radius: 0 0 12px 12px; box-shadow: 0 2px 8px rgba(1,150,63,0.08); max-width: 480px; margin: 0 auto;\">"
                + "<h2 style=\"color: #01963f;\">¡Bienvenido a nuestra app!</h2>"
                + "<p style=\"font-size: 16px; color: #000000;\">Por favor, ingresa el siguiente código de verificación para continuar:</p>"
                + "<div style=\"background: #01963f; color: #ffffff; padding: 20px; border-radius: 8px; margin: 24px 0; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 2px;\">"
                + nuevoCodigo
                + "</div>"
                + "</div>"
                + "<div style=\"background: #000000;\">"
                + "<svg viewBox='0 0 1440 150' preserveAspectRatio='none' xmlns='http://www.w3.org/2000/svg' style='display:block;'><path d='M0,75 C480,0 960,150 1440,75 L1440,150 L0,150 Z' fill='#01963f'/></svg>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    mimeMessage, true, "UTF-8");
            helper.setTo(correo);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nuevoCodigo;
    }

    @Override
    public boolean verificarCodigo(String correo, String codigoIngresado) {
        String codigoGuardado = codigosPorCorreo.get(correo);
        if (codigoGuardado != null && codigoGuardado.equalsIgnoreCase(codigoIngresado)) {
            codigosPorCorreo.remove(correo); // opcional: eliminar una vez validado
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Eliminar todas las reservas asociadas al usuario antes de eliminar el usuario
        List<Reservas> reservas = new java.util.ArrayList<>();
        reservasRepository.findAll().forEach(reserva -> {
            if (reserva.getUsuario() != null && reserva.getUsuario().getId().equals(id)) {
                reservas.add(reserva);
            }
        });
        for (Reservas reserva : reservas) {
            reservasRepository.deleteById(reserva.getId());
        }
        usuarioRepository.deleteById(id);
    }

    private String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            codigo.append(caracteres.charAt(rand.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

}
