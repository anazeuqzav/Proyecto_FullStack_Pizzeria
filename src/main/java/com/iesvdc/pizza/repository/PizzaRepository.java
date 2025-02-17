package com.iesvdc.pizza.repository;

import com.iesvdc.pizza.entity.Pizza;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends MongoRepository<Pizza, String> {
    List<Pizza> findByDisponibleTrue(); // Buscar solo pizzas disponibles
}

