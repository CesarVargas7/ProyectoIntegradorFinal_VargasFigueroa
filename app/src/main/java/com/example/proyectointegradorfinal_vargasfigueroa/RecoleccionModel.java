package com.example.proyectointegradorfinal_vargasfigueroa;

public class RecoleccionModel {
    private int id;
    private String direccion;
    private double cantidadAceite;
    private int puntos;

    public RecoleccionModel(int id, String direccion, double cantidadAceite, int puntos) {
        this.id = id;
        this.direccion = direccion;
        this.cantidadAceite = cantidadAceite;
        this.puntos = puntos;
    }

    public int getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getCantidadAceite() {
        return cantidadAceite;
    }

    public int getPuntos() {
        return puntos;
    }
}