package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "Invoices")
@Data
public class InvoiceEntity extends AbstractEntity {

    private BigDecimal totalCost;
    private String vnpTxtRef;
    private String fullName;
    private String phone;

    @ManyToOne
	@JoinColumn(name = "appointmentId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private AppointmentEntity appointmentEntity;

    @ManyToOne
    @JoinColumn(name = "patientId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private PatientEntity patient;

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getVnpTxtRef() {
        return vnpTxtRef;
    }

    public void setVnpTxtRef(String vnpTxtRef) {
        this.vnpTxtRef = vnpTxtRef;
    }

    public AppointmentEntity getAppointmentEntity() {
        return appointmentEntity;
    }

    public void setAppointmentEntity(AppointmentEntity appointmentEntity) {
        this.appointmentEntity = appointmentEntity;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }
}
