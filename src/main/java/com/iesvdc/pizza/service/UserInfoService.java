package com.iesvdc.pizza.service;

import com.iesvdc.pizza.entity.UserInfo;
import com.iesvdc.pizza.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Servicio para la gestión de usuarios en el sistema.
 * Esta clase implementa la interfaz UserDetailsService de Spring Security
 * para la autenticación y administración de usuarios.
 */
@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Carga un usuario por su nombre de usuario.
     * @param username Nombre de usuario a buscar.
     * @return Los detalles del usuario si existe.
     * @throws UsernameNotFoundException Si el usuario no es encontrado en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userDetail = repository.findByUsername(username);

        // Convertir UserInfo a UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Agrega un nuevo usuario al sistema.
     * @param userInfo Objeto que contiene la información del usuario a registrar.
     * @return Mensaje de éxito si el usuario es registrado correctamente.
     * @throws ResponseStatusException Si el nombre de usuario ya está en uso.
     */
    public String addUser(UserInfo userInfo) {
        // Verificar si el usuario ya existe
        if (repository.findByUsername(userInfo.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso.");
        }

        // Codificar la contraseña antes de guardarla
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Usuario registrado exitosamente";
    }
}
