package com.corhuila.backReservasUH.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.corhuila.backReservasUH.models.Salas;
import com.corhuila.backReservasUH.services.ISalasService;

@CrossOrigin(origins = { "https://localhost" })
@RestController
@RequestMapping("/api")
public class SalaRestController {

    @Autowired
    ISalasService salasService;

    @GetMapping("/salas")
    public List<Salas> listarVehiculos() {
        return salasService.findAll();
    }

    @GetMapping("/salas/{id}")
    public Salas obtenerVehiculo(@PathVariable Long id) {
        return salasService.findById(id);
    }

    @PostMapping("/salas")
    @ResponseStatus(HttpStatus.CREATED)
    public Salas registrarVehiculo(@RequestBody Salas salas) {
        return salasService.save(salas);
    }

    @DeleteMapping("/salas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarVehiculo(@PathVariable Long id) {
        salasService.delete(id);
    }

}
