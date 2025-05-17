package com.example.proyectointegradorfinal_vargasfigueroa;

public class RecomendacionModel {
    private String titulo;
    private String descripcion;

    public RecomendacionModel(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}