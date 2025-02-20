package com.iesvdc.pizza.controller;


import com.iesvdc.pizza.entity.AuthRequest;
import com.iesvdc.pizza.entity.UserInfo;
import com.iesvdc.pizza.repository.UserInfoRepository;
import com.iesvdc.pizza.service.JwtService;
import com.iesvdc.pizza.service.UserInfoService;
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

import java.util.Map;

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

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser( @Valid @RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public String userProfile() {
        return "Welcome to Cliente Profile";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

     //Version alternativa del generateToken:
     @PostMapping("/generateToken")
     public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
         Authentication authentication = authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
         );

         if (authentication.isAuthenticated()) {
             // Buscar usuario en MongoDB
             UserInfo user = userInfoRepository.findByUsername(authRequest.getUsername())
                     .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

             // Generar token con el ID del usuario
             String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRoles());

             // Obtener rol del usuario
             String redirectUrl = "/login"; // Valor por defecto
             if (user.getRoles().contains("CLIENTE")) {
                 redirectUrl = "/auth/pizzas";
             } else if (user.getRoles().contains("ADMIN")) {
                 redirectUrl = "/auth/admin";
             }

             return ResponseEntity.ok(Map.of("token", token, "message", "Authentication successful", "redirectUrl", redirectUrl));
         } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
         }
     }

}