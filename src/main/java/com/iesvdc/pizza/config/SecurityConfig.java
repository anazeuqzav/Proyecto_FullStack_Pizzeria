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


/**
 * Configuración de seguridad para la aplicación PomPizza.
 * Define la seguridad de las rutas, el manejo de autenticación JWT y la configuración de CORS.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthFilter authFilter;

    /**
     * Configura el servicio de detalles de usuario.
     * @return el servicio de detalles de usuario
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService();
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * @param http el objeto HttpSecurity para configurar las reglas de seguridad
     * @return la cadena de filtros de seguridad
     * @throws Exception en caso de error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/auth/checkUsername/**", "/auth/register", "/auth/login", "/", "/auth/welcome", "/auth/addNewUser", "/auth/generateToken").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .requestMatchers("/auth/user/**", "/auth/pizzas", "/auth/hacerPedido", "/auth/mis_pedidos").hasRole("CLIENTE")
                        .requestMatchers("/auth/admin/**", "/auth/panel_admin", "/auth/agregar_pizza", "/auth/editar_pizza/{id}").hasRole("ADMIN")

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
                        .anyRequest().authenticated()
                )
                // Control de acceso denegado
                .exceptionHandling(exception -> exception.accessDeniedPage("/error/acceso-denegado"))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el codificador de contraseñas.
     * @return el codificador de contraseñas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación.
     * @return el proveedor de autenticación DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Configura el gestor de autenticación.
     * @param config la configuración de autenticación
     * @return el gestor de autenticación
     * @throws Exception en caso de error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura la política de CORS para permitir peticiones del frontend.
     * @return la configuración de CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500")); // Para liveserver
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
