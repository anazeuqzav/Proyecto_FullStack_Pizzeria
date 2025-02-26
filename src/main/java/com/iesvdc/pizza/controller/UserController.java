package com.iesvdc.pizza.controller;


import com.iesvdc.pizza.entity.AuthRequest;
import com.iesvdc.pizza.entity.UserInfo;
import com.iesvdc.pizza.repository.UserInfoRepository;
import com.iesvdc.pizza.service.JwtService;
import com.iesvdc.pizza.service.UserInfoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controlador REST que maneja las solicitudes relacionadas con la autenticación y la gestión de usuarios
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Endpoint público que no requiere autenticación.
     * Devuelve un mensaje de bienvenida.
     * @return Mensaje de bienvenida.
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    /**
     * Verifica si un nombre de usuario ya está en uso.
     * @param username Nombre de usuario a verificar.
     * @return ResponseEntity con estado HTTP 200 si el nombre de usuario está disponible,
     *         o estado HTTP 400 con un mensaje de error si ya está en uso.
     */
    @GetMapping("/checkUsername")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userInfoRepository.findByUsername(username).isPresent();
        if (exists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario ya está en uso.");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param userInfo Objeto UserInfo con los datos del nuevo usuario.
     * @return ResponseEntity con un mensaje de éxito si el usuario se registra correctamente,
     *         o un mensaje de error con el estado HTTP correspondiente si falla el registro.
     */
    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody UserInfo userInfo) {
        try {
            String message = service.addUser(userInfo);
            return ResponseEntity.ok(message);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    /**
     * Endpoint accesible solo para usuarios con el rol 'CLIENTE'.
     * Devuelve un mensaje de bienvenida al perfil de cliente.
     * @return Mensaje de bienvenida al perfil de cliente.
     */
    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String userProfile() {
        return "Welcome to Cliente Profile";
    }

    /**
     * Endpoint accesible solo para usuarios con el rol 'ADMIN'.
     * Devuelve un mensaje de bienvenida al perfil de administrador.
     * @return Mensaje de bienvenida al perfil de administrador.
     */
    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    /**
     * Autentica a un usuario y genera un token JWT si las credenciales son válidas.
     * Además, configura una cookie con el token JWT para su uso posterior.
     * @param authRequest Objeto AuthRequest con las credenciales del usuario (username y password).
     * @param response HttpServletResponse para configurar la cookie con el token JWT.
     * @return ResponseEntity con un mensaje de éxito, el token JWT y una URL de redirección
     *         si la autenticación es exitosa, o un mensaje de error con estado HTTP 401 si falla.
     */
    @PostMapping("/generateToken")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserInfo user = userInfoRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRoles());

            // Configurar cookie con el token
            Cookie jwtCookie = new Cookie("JWT-TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/"); // Disponible para toda la app
            jwtCookie.setMaxAge(24 * 60 * 60); // Expira en 1 día
            response.addCookie(jwtCookie);

            String redirectUrl = user.getRoles().contains("CLIENTE") ? "/auth/pizzas" : "/auth/panel_admin";

            return ResponseEntity.ok(Map.of(
                    "message", "Autenticación exitosa",
                    "token", token,  // Añade el token en la respuesta
                    "redirectUrl", redirectUrl
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}