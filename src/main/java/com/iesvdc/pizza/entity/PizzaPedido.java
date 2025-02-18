package com.iesvdc.pizza.entity;

public class PizzaPedido {
    private String nombre;
    private double precio;

    // Constructor vacío
    public PizzaPedido() {}

    // Constructor con parámetros
    public PizzaPedido(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
