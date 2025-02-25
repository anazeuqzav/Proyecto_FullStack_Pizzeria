package com.iesvdc.pizza.filter;

import com.iesvdc.pizza.service.JwtService;
import com.iesvdc.pizza.service.UserInfoService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Retrieve the Authorization header
        String token = null;
        String username = null;
        String role = null;

        // Intentar obtener el token desde la cabecera Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Si no hay token en la cabecera, buscar en las cookies
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT-TOKEN".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (token != null) {
            try {
                username = jwtService.extractUsername(token);
                role = jwtService.extractRole(token);
                System.out.println("Token recibido: " + token);
                System.out.println("Usuario: " + username);
                System.out.println("Rol: " + role);
            } catch (ExpiredJwtException e) {
                // Si el token expiró y la ruta es pública (login, register), limpiar la cookie y dejar pasar
                System.out.println("Token expirado!!!!!!!!!!!!");
                Cookie jwtCookie = new Cookie("JWT-TOKEN", null);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false); // Cambia a true si usas HTTPS
                jwtCookie.setPath("/"); // Disponible para toda la app
                jwtCookie.setMaxAge(0); // Expira en 1 día
                response.addCookie(jwtCookie);

            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                System.out.println("Roles asignados: " + authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Autenticación establecida en SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
            }
        }

        filterChain.doFilter(request, response);
        }
    }

