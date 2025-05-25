package com.ISP392.demo.service;

import com.ISP392.demo.entity.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface RoleService {
    Optional<RoleEntity> findById(Long id);
}

