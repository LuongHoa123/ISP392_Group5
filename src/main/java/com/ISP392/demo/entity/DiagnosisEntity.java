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
@Table(name = "Diagnosis")
@Data
public class DiagnosisEntity extends AbstractEntity {
    private String content;

	@ManyToOne
	@JoinColumn(name = "appointmentId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private AppointmentEntity appointment;

	@ManyToOne
	@JoinColumn(name = "serviceId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private ServiceEntity service;

}
