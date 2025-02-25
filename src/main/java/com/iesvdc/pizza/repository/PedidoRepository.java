package com.iesvdc.pizza.repository;

import com.iesvdc.pizza.entity.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de pedidos en la base de datos MongoDB.
 * Proporciona métodos para realizar operaciones CRUD sobre la colección de pedidos.
 */
@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    // Busca una lista de pedidos asociados a un cliente específico.
    List<Pedido> findByClienteUsername(String clienteUsername);

}
