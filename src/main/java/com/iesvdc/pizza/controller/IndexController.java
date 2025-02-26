package com.iesvdc.pizza.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Controlador para la página de inicio de la aplicación PomPizza.
 * Redirige automáticamente a la página de inicio de sesión.
 */
@Controller
public class IndexController {

    /**
     * Redirige la raíz de la aplicación a la página de inicio de sesión.
     *
     * @return una redirección a la URL de autenticación
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/auth/login";
    }
}