package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Appointments")
@Data
public class AppointmentEntity extends AbstractEntity {

    @Column(name = "appointment_date_time")
    protected LocalDateTime appointmentDateTime;

    @Column(name = "reason")
    private String reason;

    private String name;

    private String phoneNumber;

    private Integer age;

    private String email;

	@ManyToOne
	@JoinColumn(name = "patientId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private PatientEntity patient;


    @ManyToOne
    @JoinColumn(name = "doctorId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private DoctorEntity doctor;

    @ManyToOne
    @JoinColumn(name = "roomId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private RoomEntity room;

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public DoctorEntity getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntity doctor) {
        this.doctor = doctor;
    }
}
