package com.ISP392.demo.repository;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.RecepEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    ReviewEntity findByAppointmentIdAndPatientId(Long appointmentId, Long patientId);

    @Query("SELECT r FROM ReviewEntity r ORDER BY r.star DESC, r.createdAt DESC")
    List<ReviewEntity> findTop3ByOrderByStarDesc();

    @Query("SELECT r FROM ReviewEntity r WHERE r.appointment.doctor = :doctor")
    List<ReviewEntity> findByDoctor(@Param("doctor") DoctorEntity doctor);

    @Query("SELECT AVG(r.star) FROM ReviewEntity r WHERE r.appointment.doctor = :doctor")
    Double getAverageRatingByDoctor(@Param("doctor") DoctorEntity doctor);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.appointment.doctor = :doctor")
    Long getTotalReviewsByDoctor(@Param("doctor") DoctorEntity doctor);
}
