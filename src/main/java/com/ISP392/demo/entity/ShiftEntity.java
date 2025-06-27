package com.ISP392.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "Shifts")
@Data
public class ShiftEntity extends AbstractEntity {

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @EqualsAndHashCode.Exclude
    private DoctorEntity doctor;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    @EqualsAndHashCode.Exclude
    private NurseEntity nurseEntity;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DoctorEntity getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntity doctor) {
        this.doctor = doctor;
    }

    public NurseEntity getNurseEntity() {
        return nurseEntity;
    }

    public void setNurseEntity(NurseEntity nurseEntity) {
        this.nurseEntity = nurseEntity;
    }
}
