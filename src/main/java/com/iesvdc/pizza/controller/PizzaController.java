package com.iesvdc.pizza.controller;

import com.iesvdc.pizza.entity.Pizza;
import com.iesvdc.pizza.service.PizzaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar pizzas en la aplicación.
 * Proporciona endpoints para realizar operaciones CRUD sobre las pizzas.
 *
 * @author Ana Vazquez
 * @version 1.0, 17-02
 */
@RestController
@RequestMapping("/api/pizzas")
public class PizzaController {
    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    /**
     * Método que obtiene la lista de todas las pizzas
     * @return la lista de pizzas y el código de estado 200 OK.
     */
    @GetMapping
    public List<Pizza> obtenerTodasLasPizzas() {
        return pizzaService.obtenerTodasLasPizzas();
    }

    /**
     * Método que obtiene la lista de todas las pizzas que se encuentran disponibles
     * @return lista de pizzas y el código de estado 200 OK
     */
    @GetMapping("/disponibles")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public List<Pizza> obtenerPizzasDisponibles() {
        return pizzaService.obtenerPizzasDisponibles();
    }

    /**
     * Método que obtiene una pizza por su identificador
     * @param id identificador de la pizza que se desea buscar
     * @return la pizza encontrada y el código 200 OK, o 404 Not Found si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pizza> obtenerPizzaPorId(@PathVariable String id) {
        return pizzaService.obtenerPizzaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Método que añade una pizza a la base de datos
     * @param pizza objeto pizza que se quiere añadir
     * @return la pizza creada y el código 201 created
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Pizza> agregarPizza(@RequestBody Pizza pizza) {
        return ResponseEntity.ok(pizzaService.agregarPizza(pizza));
    }

    /**
     * Método que actualiza los datos de una pizza en la base de datos
     * @param id identificador de la pizza que se desea actualizar
     * @param pizza objeto pizza con los datos nuevos
     * @return la pizza actualizada y código 200 OK, o 404 Not Found si la pizza no existe.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Pizza> actualizarPizza(@PathVariable String id, @RequestBody Pizza pizza) {
        return ResponseEntity.ok(pizzaService.actualizarPizza(id, pizza));
    }

    /**
     * Método para eliminar una pizza por su identificador
     * @param id identificador de la pizza que se quiere eliminar
     * @return código 204 si se ha eliminado correctamente, o 404 si no se ha encontrado la pizza.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarPizza(@PathVariable String id) {
        pizzaService.eliminarPizza(id);
        return ResponseEntity.noContent().build();
    }
}