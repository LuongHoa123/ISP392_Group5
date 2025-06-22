package com.ISP392.demo.repository;

import com.ISP392.demo.entity.LogsEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SpringBootApplication
public interface LogsRepository extends JpaRepository<LogsEntity, Long> {

}
