package com.ISP392.demo.repository;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.RecepEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    @Query("SELECT d FROM DoctorEntity d WHERE " +
            "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<DoctorEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    DoctorEntity findByUser(UserEntity user);

}
