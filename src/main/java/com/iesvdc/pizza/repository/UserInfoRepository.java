package com.iesvdc.pizza.repository;

import com.iesvdc.pizza.entity.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    Optional<UserInfo> findByUsername(String username);
}