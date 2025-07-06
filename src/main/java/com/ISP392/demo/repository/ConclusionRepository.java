package com.ISP392.demo.repository;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.ConclusionEntity;
import com.ISP392.demo.entity.DiagnosisEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ConclusionRepository extends JpaRepository<ConclusionEntity, Long> {
}
