package com.ISP392.demo.repository;

import com.ISP392.demo.entity.AppointmentServiceEntity;
import com.ISP392.demo.entity.ServiceEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface AppointmentServiceRepository extends JpaRepository<AppointmentServiceEntity, Long> {
    List<AppointmentServiceEntity> findByAppointmentId(Long appointmentId);

    void deleteByAppointmentIdAndServiceId(Long appointmentId, Long serviceId);
}
