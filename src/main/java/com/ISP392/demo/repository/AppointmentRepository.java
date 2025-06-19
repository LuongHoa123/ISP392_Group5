package com.ISP392.demo.repository;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.PatientEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByDoctor(DoctorEntity doctorEntity);
    List<AppointmentEntity> findByPatient(PatientEntity patientEntity);
    boolean existsByDoctorIdAndAppointmentDateTime(Long doctorId, LocalDateTime dateTime);
    boolean existsByRoomIdAndAppointmentDateTime(Long roomId, LocalDateTime dateTime);

    List<AppointmentEntity> findTop5ByStatusOrderByAppointmentDateTimeDesc(Integer status);

    List<AppointmentEntity> findTop10ByStatusOrderByAppointmentDateTimeDesc(Integer status);

    @Query("SELECT MONTH(a.appointmentDateTime), COUNT(a) " +
            "FROM AppointmentEntity a " +
            "WHERE YEAR(a.appointmentDateTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(a.appointmentDateTime)")
    List<Object[]> countAppointmentsByMonthInCurrentYear();

    @Query("SELECT a.status, COUNT(a) " +
            "FROM AppointmentEntity a " +
            "WHERE MONTH(a.appointmentDateTime) = MONTH(CURRENT_DATE) AND YEAR(a.appointmentDateTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY a.status")
    List<Object[]> countAppointmentStatusForCurrentMonth();

    @Query("SELECT MONTH(a.appointmentDateTime), COUNT(a) " +
            "FROM AppointmentEntity a " +
            "WHERE YEAR(a.appointmentDateTime) = :year " +
            "GROUP BY MONTH(a.appointmentDateTime)")
    List<Object[]> countAppointmentsByMonth(@Param("year") int year);


    @Query(
            value = "SELECT DATE(a.appointment_date_time) AS day, COUNT(*) AS cnt " +
                    "FROM appointments a " +
                    "WHERE YEAR(a.appointment_date_time) = :year " +
                    "GROUP BY DATE(a.appointment_date_time)",
            nativeQuery = true)
    List<Object[]> countAppointmentsByDay(@Param("year") int year);

    @Query(value = """
    SELECT 
        DAYOFWEEK(a.appointment_date_time) AS weekday,
        COUNT(*) AS count
    FROM appointments a
    WHERE YEAR(a.appointment_date_time) = :year
    GROUP BY DAYOFWEEK(a.appointment_date_time)
""", nativeQuery = true)
    List<Object[]> countAppointmentsByWeekday(@Param("year") int year);




}
