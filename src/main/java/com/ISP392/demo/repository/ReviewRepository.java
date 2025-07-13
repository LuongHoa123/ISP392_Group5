package com.ISP392.demo.repository;

import com.ISP392.demo.entity.RecepEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    ReviewEntity findByAppointmentIdAndPatientId(Long appointmentId, Long patientId);

    @Query("SELECT r FROM ReviewEntity r ORDER BY r.star DESC, r.createdAt DESC")
    List<ReviewEntity> findTop3ByOrderByStarDesc();
}
