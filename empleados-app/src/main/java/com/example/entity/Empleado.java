package com.example.entity;

public class Empleado {

    private Long id;
    private String nombre;
    private String departamento;
    private Usuario usuario; // Agregar el campo Usuario

    // Constructor vacío (sin parámetros)
    public Empleado() {
    }

    // Constructor con parámetros
    public Empleado(Long id, String nombre, String departamento, Usuario usuario) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario; // Este es el setter que faltaba
    }
}
