package com.iesvdc.pizza.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/pizzas")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String catalogo(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "carta_pizzas";
    }

    @GetMapping("/hacerPedido")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String hacerPedido(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "hacer_pedido";
    }

    @GetMapping("/mis_pedidos")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String misPedidos(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "mis_pedidos";
    }

    @GetMapping("/panel_admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String panelAdmin(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "panel_admin";
    }

    @GetMapping("/agregar_pizza")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String agregarPizza(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "agregar_pizza";
    }

    @GetMapping("/editar_pizza/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editarPizza(@RequestParam("id") String id, Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        model.addAttribute("pizzaId", id);
        return "editar_pizza";
    }




    private void agregarTokenAlModelo(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        model.addAttribute("token", token);
    }
}
