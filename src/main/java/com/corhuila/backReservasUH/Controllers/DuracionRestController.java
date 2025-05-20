package com.corhuila.backReservasUH.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corhuila.backReservasUH.models.Duracion;
import com.corhuila.backReservasUH.services.IDuracionService;

@CrossOrigin(origins = { "https://localhost" })
@RestController
@RequestMapping("/api")
public class DuracionRestController {

    @Autowired
    private IDuracionService duracionService;

    @PostMapping("/{reservaId}/finalizar")
    public ResponseEntity<Duracion> finalizar(@PathVariable Long reservaId) {
        return ResponseEntity.ok(duracionService.finalizarServicio(reservaId));
    }

    @GetMapping("/duracion/{reservaId}")
    public ResponseEntity<Duracion> getByReservaId(@PathVariable Long reservaId) {
        Optional<Duracion> duracion = duracionService.findByReservaId(reservaId);
        return duracion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/duracion")
    public ResponseEntity<List<Duracion>> findAllDuraciones() {
        return ResponseEntity.ok(duracionService.findAllDuraciones());
    }

}
