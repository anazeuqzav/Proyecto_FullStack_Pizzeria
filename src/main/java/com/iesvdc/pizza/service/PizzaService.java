package com.iesvdc.pizza.service;


import com.iesvdc.pizza.entity.Pizza;
import com.iesvdc.pizza.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con las pizzas.
 * Proporciona métodos para crear, actualizar, eliminar y obtener pizzas
 * desde la base de datos.
 */
@Service
public class PizzaService {
    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    /**
     * Método para obtener todas las pizzas en la bbd
     * @return lista de pizzas en la bbdd
     */
    public List<Pizza> obtenerTodasLasPizzas() {
        return pizzaRepository.findAll();
    }

    /**
     * Método para obtener solo las pizzas que se encuentran disponibles
     * @return una lista con las pizzas disponibles
     */
    public List<Pizza> obtenerPizzasDisponibles() {
        return pizzaRepository.findByDisponibleTrue();
    }

    /**
     * Método para obtener una pizza por un identificador
     * @param id identificador único de la pizza
     * @return el objeto pizza encontrado
     */
    public Optional<Pizza> obtenerPizzaPorId(String id) {
        return pizzaRepository.findById(id);
    }

    /**
     * Método para añadir una pizza a la base de datos
     * @param pizza objeto pizza a añadir
     */
    public Pizza agregarPizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    /**
     * Método para actualizar los atributos de una pizza
     * @param id identificador de la pizza a actualizar
     * @param pizzaActualizada objeto con los datos nuevos que se quieren actualizar
     */
    public Pizza actualizarPizza(String id, Pizza pizzaActualizada) {
        Optional<Pizza> optionalPizza = pizzaRepository.findById(id);

        if (optionalPizza.isPresent()) {
            Pizza pizza = optionalPizza.get();
            pizza.setNombre(pizzaActualizada.getNombre());
            pizza.setDescripcion(pizzaActualizada.getDescripcion());
            pizza.setPrecio(pizzaActualizada.getPrecio());
            pizza.setIngredientes(pizzaActualizada.getIngredientes());
            pizza.setDisponible(pizzaActualizada.isDisponible());

            return pizzaRepository.save(pizza);

        } else {
            throw new RuntimeException("Pizza no encontrada");
        }
    }

    /**
     * Método para eliminar una pizza de la base de datos
     * @param id identificador de la pizza que se desea eliminar
     */
    public void eliminarPizza(String id) {
        pizzaRepository.deleteById(id);
    }
}