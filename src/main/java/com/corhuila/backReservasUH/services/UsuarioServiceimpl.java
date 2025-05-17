package com.corhuila.backReservasUH.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import com.corhuila.backReservasUH.models.Usuario;
import com.corhuila.backReservasUH.repositories.IUsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioServiceimpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

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

        // Crear el mensaje de correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Código de verificación de cuenta");
        mensaje.setText("Tu nuevo código de verificación es: " + nuevoCodigo);

        // Enviar el correo
        mailSender.send(mensaje);

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
