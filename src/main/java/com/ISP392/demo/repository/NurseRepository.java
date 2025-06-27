package com.ISP392.demo.repository;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface NurseRepository extends JpaRepository<NurseEntity, Long> {
    NurseEntity findByUser(UserEntity user);
    Optional<NurseEntity> findByPhoneNumber(String phone);
}
