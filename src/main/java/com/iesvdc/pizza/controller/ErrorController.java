package com.iesvdc.pizza.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para gestionar los errores en la aplicación PomPizza.
 * Maneja accesos denegados y redirige a las vistas correspondientes según el rol del usuario.
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    /**
     * Maneja el acceso denegado y redirige a la vista correspondiente según el rol del usuario.
     * @param authentication la autenticación del usuario actual
     * @return la vista correspondiente según el rol del usuario
     */
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
