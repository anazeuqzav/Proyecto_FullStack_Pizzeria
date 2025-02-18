package com.iesvdc.pizza.service;

import com.iesvdc.pizza.entity.Pedido;
import com.iesvdc.pizza.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPedidoPorId(String id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPedidosPorCliente(String usernameCliente) {
        return pedidoRepository.findByUsername(usernameCliente);
    }

    public Pedido actualizarPedido(String id, Pedido pedido) {
        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(String id) {
        pedidoRepository.deleteById(id);
    }
}