package com.ISP392.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.PatientEntity;

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


    Page<AppointmentEntity> findByAppointmentDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);


    @Query("SELECT FUNCTION('DAY', a.appointmentDateTime), SUM(a.totalCost) " +
            "FROM AppointmentEntity a " +
            "WHERE FUNCTION('MONTH', a.appointmentDateTime) = :month " +
            "AND FUNCTION('YEAR', a.appointmentDateTime) = :year " +
            "GROUP BY FUNCTION('DAY', a.appointmentDateTime) " +
            "ORDER BY FUNCTION('DAY', a.appointmentDateTime)")
    List<Object[]> getDailyRevenueByMonth(@Param("month") int month, @Param("year") int year);

    void deleteByDoctorAndPatient(DoctorEntity doctor, PatientEntity patient);

    List<AppointmentEntity> findByDoctorAndAppointmentDateTimeBetween(DoctorEntity doctor, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a.status, COUNT(a) FROM AppointmentEntity a WHERE a.doctor = :doctor AND MONTH(a.appointmentDateTime) = :month AND YEAR(a.appointmentDateTime) = :year GROUP BY a.status")
    List<Object[]> countAppointmentStatusForDoctorByMonth(@Param("doctor") DoctorEntity doctor, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.doctor = :doctor AND a.status = :status AND MONTH(a.appointmentDateTime) = :month AND YEAR(a.appointmentDateTime) = :year")
    List<AppointmentEntity> findByDoctorAndStatusAndMonthAndYear(@Param("doctor") DoctorEntity doctor, @Param("status") int status, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.doctor = :doctor AND a.status IN :statuses AND MONTH(a.appointmentDateTime) = :month AND YEAR(a.appointmentDateTime) = :year")
    List<AppointmentEntity> findByDoctorAndStatusInAndMonthAndYear(@Param("doctor") DoctorEntity doctor, @Param("statuses") List<Integer> statuses, @Param("month") int month, @Param("year") int year);

    boolean existsByPatientAndAppointmentDateTimeBetween(PatientEntity patient, LocalDateTime start, LocalDateTime end);
}
