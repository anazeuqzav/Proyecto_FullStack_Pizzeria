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
        return "pizzas";
    }

    @GetMapping("/hacerPedido")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String hacerPedido(Model model, HttpServletRequest request) {
        agregarTokenAlModelo(model, request);
        return "pedido";
    }

    private void agregarTokenAlModelo(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        model.addAttribute("token", token);
    }
}
