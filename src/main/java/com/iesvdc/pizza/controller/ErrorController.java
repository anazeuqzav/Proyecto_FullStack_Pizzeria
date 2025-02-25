package com.iesvdc.pizza.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "panel_admin"; // Redirige a la página de administrador
        } else {
            return "carta_pizzas"; // Redirige a la página de cliente
        }
    }
}