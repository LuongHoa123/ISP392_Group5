package com.ISP392.demo.service.impl;


import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.repository.RoleRepository;
import com.ISP392.demo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public Optional<RoleEntity> findById(Long id) {
        return roleRepository.findById(id);
    }
}
