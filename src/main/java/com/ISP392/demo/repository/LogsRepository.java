package com.ISP392.demo.repository;

import com.ISP392.demo.entity.LogsEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface LogsRepository extends JpaRepository<LogsEntity, Long> {
    List<LogsEntity> findByContentContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(String content, String email);
}
