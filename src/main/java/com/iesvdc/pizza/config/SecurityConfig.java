package com.iesvdc.pizza.config;
import com.iesvdc.pizza.filter.JwtAuthFilter;
import com.iesvdc.pizza.service.UserInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthFilter authFilter;


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService(); // Ensure UserInfoService implements UserDetailsService
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔹 Habilitar CORS si el frontend está separado
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/auth/register", "/auth/login", "/", "/auth/welcome", "/auth/addNewUser", "/auth/generateToken").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()  // Archivos estáticos

                        // Seguridad por roles
                        .requestMatchers("/auth/user/**", "/auth/pizzas", "/auth/hacerPedido").hasRole("CLIENTE")
                        .requestMatchers("/auth/admin/**").hasRole("ADMIN")

                        // Seguridad en pizzas (ver todas permitido, modificar solo ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/pizzas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pizzas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pizzas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pizzas/**").hasRole("ADMIN")

                        // Seguridad en pedidos
                        .requestMatchers(HttpMethod.POST,  "/api/pedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET,  "/auth/pizzas","/api/pedidos/misPedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/{id}").hasRole("ADMIN")

                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 🔹 API sin estado
                .authenticationProvider(authenticationProvider()) // Proveedor de autenticación JWT
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encoding
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500")); // frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}