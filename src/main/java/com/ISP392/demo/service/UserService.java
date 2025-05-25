package com.ISP392.demo.service;


import com.ISP392.demo.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;


public interface UserService extends UserDetailsService {

    Optional<UserEntity> findByEmail(String email);

    boolean validateCredentials(String username, String password);

    UserEntity saveUser(UserEntity user);
}

