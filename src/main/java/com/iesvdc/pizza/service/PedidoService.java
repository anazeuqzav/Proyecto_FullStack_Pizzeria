package com.iesvdc.pizza.service;

import com.iesvdc.pizza.entity.Pedido;
import com.iesvdc.pizza.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de pedidos.
 * Esta clase proporciona métodos para crear, consultar, actualizar y eliminar pedidos
 * en la base de datos a través del repositorio correspondiente.
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Método para guardar un nuevo pedido en la base de datos.
     *
     * @param pedido Objeto Pedido a guardar.
     * @return Pedido guardado.
     */
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    /**
     * Método que obtiene la lista de todos los pedidos registrados.
     *
     * @return Lista de pedidos.
     */
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    /**
     * Método que obtiene un pedido específico por su ID.
     *
     * @param id Identificador del pedido.
     * @return Objeto Optional que contiene el pedido si existe.
     */
    public Optional<Pedido> obtenerPedidoPorId(String id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Método que obtiene la lista de pedidos de un cliente en función de su nombre de usuario.
     *
     * @param clienteUsername Nombre de usuario del cliente.
     * @return Lista de pedidos asociados al cliente.
     */
    public List<Pedido> obtenerPedidosPorCliente(String clienteUsername) {
        return pedidoRepository.findByClienteUsername(clienteUsername);
    }

    /**
     * Método para actualiza un pedido existente.
     *
     * @param id     Identificador del pedido a actualizar.
     * @param pedido Objeto Pedido con los nuevos datos.
     * @return Pedido actualizado.
     */
    public Pedido actualizarPedido(String id, Pedido pedido) {
        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    /**
     * Método para elimina un pedido de la base de datos por su ID.
     *
     * @param id Identificador del pedido a eliminar.
     */
    public void eliminarPedido(String id) {
        pedidoRepository.deleteById(id);
    }
}