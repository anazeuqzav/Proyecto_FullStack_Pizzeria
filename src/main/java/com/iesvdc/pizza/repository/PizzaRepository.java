package com.iesvdc.pizza.repository;

import com.iesvdc.pizza.entity.Pizza;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de pizzas en la base de datos MongoDB.
 *
 * Proporciona métodos para realizar operaciones CRUD sobre la colección de pizzas.
 */
@Repository
public interface PizzaRepository extends MongoRepository<Pizza, String> {
    // Busca y devuelve una lista de pizzas que están disponibles
    List<Pizza> findByDisponibleTrue(); // Buscar solo pizzas disponibles
}

