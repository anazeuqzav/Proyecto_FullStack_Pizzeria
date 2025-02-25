package com.iesvdc.pizza.service;

import com.iesvdc.pizza.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetails para la autenticación con Spring Security.
 * Esta clase adapta la información del usuario de la base de datos para que pueda ser utilizada
 * por el sistema de autenticación de Spring Security.
 */
public class UserInfoDetails implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    // Constructor que inicializa los detalles del usuarioa partir de un objeto Usuario
    public UserInfoDetails(UserInfo userInfo) {
        this.username = userInfo.getUsername(); // Assuming 'name' is used as 'username'
        this.password = userInfo.getPassword();
        this.authorities = List.of(userInfo.getRoles().split(","))
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        System.out.println("Cargando usuario: " + username + " con roles: " + this.authorities);

    }

    // Devuelve una lista de roles (authorities) del usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Devuelve la contraseña del usuario
    @Override
    public String getPassword() {
        return password;
    }

    // Devuelve el nombre del usuario
    @Override
    public String getUsername() {
        return username;
    }

    // Indica si la cuenta del usuario ha expirado
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica si la cuenta del usuario está bloqueada.
    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic if you need this
    }

    // Indica si las credenciales del usuario han expirado.
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic if you need this
    }

    // Indica si la cuenta del usuario está habilitada.
    @Override
    public boolean isEnabled() {
        return true; // Implement your logic if you need this
    }
}