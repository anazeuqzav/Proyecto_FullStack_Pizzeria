package com.iesvdc.pizza.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para gestionar la autenticación y las vistas principales de la aplicación PomPizza.
 * Maneja las páginas de inicio de sesión, registro, catálogo de pizzas y panel de administración.
 */
@Controller
@RequestMapping("/auth")
public class WebController {

    /**
     * Muestra la página de inicio de sesión.
     * @return la vista de inicio de sesión
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Muestra la página de registro de usuarios.
     * @return la vista de registro
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * Muestra el catálogo de pizzas para clientes.
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista del catálogo de pizzas
     */
    @GetMapping("/pizzas")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String catalogo(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "carta_pizzas";
    }

    /**
     * Muestra la página para realizar un pedido.
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista de hacer pedido
     */
    @GetMapping("/hacerPedido")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String hacerPedido(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "hacer_pedido";
    }

    /**
     * Muestra la lista de pedidos del cliente.
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista de pedidos del cliente
     */
    @GetMapping("/mis_pedidos")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String misPedidos(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "mis_pedidos";
    }

    /**
     * Muestra el panel de administración para administradores.
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista del panel de administración
     */
    @GetMapping("/panel_admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String panelAdmin(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "panel_admin";
    }

    /**
     * Muestra la página para agregar una nueva pizza (solo administradores).
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista de agregar pizza
     */
    @GetMapping("/agregar_pizza")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String agregarPizza(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "agregar_pizza";
    }

    /**
     * Muestra la página para editar una pizza existente (solo administradores).
     * @param id      el identificador de la pizza a editar
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     * @return la vista de edición de pizza
     */
    @GetMapping("/editar_pizza/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editarPizza(@RequestParam("id") String id, Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        model.addAttribute("pizzaId", id);
        return "editar_pizza";
    }

    /**
     * Agrega el token de autorización al modelo de la vista.
     * @param model   el modelo de la vista
     * @param request la solicitud HTTP
     */
    private void agregarTokenAlModelo(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        model.addAttribute("token", token);
    }
}
