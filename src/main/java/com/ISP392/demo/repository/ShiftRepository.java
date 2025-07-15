package com.ISP392.demo.repository;

import com.ISP392.demo.entity.ShiftEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SpringBootApplication
public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {
    Page<ShiftEntity> findByDoctorId(Long id, Pageable pageable);
    Page<ShiftEntity> findByNurseId(Long id, Pageable pageable);
    Page<ShiftEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<ShiftEntity> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<ShiftEntity> findByNurseIdAndStartTimeBetween(Long nurseId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT s FROM ShiftEntity s WHERE " +
            "((:doctorId IS NOT NULL AND s.doctor.id = :doctorId) OR " +
            " (:nurseId IS NOT NULL AND s.nurse.id = :nurseId)) AND " +
            "((s.startTime < :endTime AND s.endTime > :startTime)) AND " +
            "(:currentId IS NULL OR s.id <> :currentId)")
    List<ShiftEntity> findConflictingShifts(@Param("doctorId") Long doctorId,
                                            @Param("nurseId") Long nurseId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("currentId") Long currentId);

}
