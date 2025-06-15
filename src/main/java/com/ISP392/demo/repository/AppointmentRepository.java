package com.ISP392.demo.repository;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

}
