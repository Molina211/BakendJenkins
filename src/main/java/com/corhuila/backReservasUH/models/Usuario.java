package com.corhuila.backReservasUH.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_1")
    private String primernombre;

    @Column(name = "nom_2")
    private String segundonombre;

    @Column(name = "apellido_1")
    private String primerapellido;

    @Column(name = "apellido_2")
    private String segundoapellido;

    @Column(name = "correo")
    private String correo;

    @Column(name = "contraseña")
    private String contrasena;

    @Column(name = "codigo_estudiantil", unique = true)
    private Double codigo_estudiantil;

    @Column(name = "tipo_documento")
    private String tipo_documento;

    @Column(name = "num_documento", unique = true)
    private Double num_documento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimernombre() {
        return primernombre;
    }

    public void setPrimernombre(String primernombre) {
        this.primernombre = primernombre;
    }

    public String getSegundonombre() {
        return segundonombre;
    }

    public void setSegundonombre(String segundonombre) {
        this.segundonombre = segundonombre;
    }

    public String getPrimerapellido() {
        return primerapellido;
    }

    public void setPrimerapellido(String primerapellido) {
        this.primerapellido = primerapellido;
    }

    public String getSegundoapellido() {
        return segundoapellido;
    }

    public void setSegundoapellido(String segundoapellido) {
        this.segundoapellido = segundoapellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Double getCodigo_estudiantil() {
        return codigo_estudiantil;
    }

    public void setCodigo_estudiantil(Double codigo_estudiantil) {
        this.codigo_estudiantil = codigo_estudiantil;
    }

    public String getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public Double getNum_documento() {
        return num_documento;
    }

    public void setNum_documento(Double num_documento) {
        this.num_documento = num_documento;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
