package com.iesvdc.pizza.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * Representa un pedido en la base de datos MongoDB
 */
@Data
@Document(collection = "pedidos")
public class Pedido {

    // Atributos
    @Id
    private String id;
    private String clienteUsername;
    private List<PizzaPedido> pizzas;
    private double total;

    private Date fecha;
    private String estado;

    // Constructor vacío
    public Pedido() {}

    // Constructor con parámetros
    public Pedido(String clienteId, List<PizzaPedido> pizzas, String estado) {
        this.clienteUsername = clienteId;
        this.pizzas = pizzas;
        this.total = pizzas.stream().mapToDouble(PizzaPedido::getPrecio).sum(); // cálculo automático del total
        this.fecha = new Date();
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getClienteUsername() { return clienteUsername; }

    public void setClienteUsername(String clienteUsername) { this.clienteUsername = clienteUsername; }

    public List<PizzaPedido> getPizzas() { return pizzas; }

    public void setPizzas(List<PizzaPedido> pizzas) { this.pizzas = pizzas; }

    public double getTotal() { return total; }

    public void setTotal(double total) { this.total = total; }

    public Date getFecha() { return fecha; }

    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }
}