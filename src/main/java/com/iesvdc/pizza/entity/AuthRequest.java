package com.iesvdc.pizza.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una solicitud de autenticación.
 *
 * Contiene las credenciales del usuario necesarias para la autenticación.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    // Atributos
    private String username;
    private String password;

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}