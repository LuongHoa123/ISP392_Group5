package com.ISP392.demo.repository;

import com.ISP392.demo.entity.ShiftEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@SpringBootApplication
public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {
    Page<ShiftEntity> findByDoctorId(Long id, Pageable pageable);
    Page<ShiftEntity> findByNurseId(Long id, Pageable pageable);
    Page<ShiftEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<ShiftEntity> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<ShiftEntity> findByNurseIdAndStartTimeBetween(Long nurseId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
