package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

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

    private BigDecimal totalCost;

    private BigDecimal payCost;
    private Integer paymentStatus;


    private String noteCancel;

    private String conclusion;

    private String prescription;

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

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<DiagnosisEntity> diagnosisEntities;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private ConclusionEntity conclusionEntity;


    public BigDecimal getPayCost() {
        return payCost;
    }

    public void setPayCost(BigDecimal payCost) {
        this.payCost = payCost;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getNoteCancel() {
        return noteCancel;
    }

    public void setNoteCancel(String noteCancel) {
        this.noteCancel = noteCancel;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public Set<DiagnosisEntity> getDiagnosisEntities() {
        return diagnosisEntities;
    }

    public void setDiagnosisEntities(Set<DiagnosisEntity> diagnosisEntities) {
        this.diagnosisEntities = diagnosisEntities;
    }

    public ConclusionEntity getConclusionEntity() {
        return conclusionEntity;
    }

    public void setConclusionEntity(ConclusionEntity conclusionEntity) {
        this.conclusionEntity = conclusionEntity;
    }

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
