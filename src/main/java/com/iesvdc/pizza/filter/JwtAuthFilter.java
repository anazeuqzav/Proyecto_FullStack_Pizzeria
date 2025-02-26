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

/**
 * Filtro de autenticación JWT para interceptar y validar tokens en cada solicitud.
 * Se encarga de extraer el token desde la cabecera Authorization o desde una cookie,
 * validar su contenido y establecer la autenticación en el contexto de seguridad.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userDetailsService;

    /**
     * Intercepta cada solicitud entrante, verifica la presencia de un token JWT,
     * lo valida y establece la autenticación en el contexto de seguridad si es válido.
     * @param request la solicitud HTTP entrante
     * @param response la respuesta HTTP
     * @param filterChain la cadena de filtros de la solicitud
     * @throws ServletException en caso de error en la ejecución del filtro
     * @throws IOException en caso de error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        String role = null;

        // Obtener el token desde la cabecera Authorization
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
                System.out.println("Token recibido: " + token); // para depuracion
                System.out.println("Usuario: " + username); // para depuracion
                System.out.println("Rol: " + role); // para depuracion
            } catch (ExpiredJwtException e) {
                // Si el token expiró, limpiar la cookie y permitir continuar
                System.out.println("Token expirado!");
                Cookie jwtCookie = new Cookie("JWT-TOKEN", null);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(0);
                response.addCookie(jwtCookie);
            }
        }

        // Validar el usuario y establecer autenticación
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
