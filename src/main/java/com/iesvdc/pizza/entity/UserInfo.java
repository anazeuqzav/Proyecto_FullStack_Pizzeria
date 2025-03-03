package com.iesvdc.pizza.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa la información de un usuario en la base de datos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuarios")
public class UserInfo {

    // Atributos
    @Id
    private String id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String username;
    private String email;
    @NotBlank(message = "Debes introducir una contraseña")
    private String password;
    private String roles; // CLIENTE Y ADMIN

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}