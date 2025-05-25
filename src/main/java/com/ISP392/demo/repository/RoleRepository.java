package com.ISP392.demo.repository;

import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.enums.RoleEnum;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findById(Long id);

    RoleEntity findByName(RoleEnum roleEnum);
}
