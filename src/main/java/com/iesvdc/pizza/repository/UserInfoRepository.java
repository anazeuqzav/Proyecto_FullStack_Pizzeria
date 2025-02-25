package com.iesvdc.pizza.repository;

import com.iesvdc.pizza.entity.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de información de usuarios en la base de datos MongoDB.
 *
 * Proporciona métodos para realizar operaciones CRUD sobre la colección de usuarios.
 */
@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    // Busca un usuario por su nombre de usuario.
    Optional<UserInfo> findByUsername(String username);
}