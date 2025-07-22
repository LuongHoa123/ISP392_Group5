package com.ISP392.demo.repository;

import com.ISP392.demo.entity.RequestEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Optional<RequestEntity> findByUser(UserEntity user);
}
