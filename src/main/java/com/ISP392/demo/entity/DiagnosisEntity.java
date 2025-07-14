package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "Diagnosis")
@Data
public class DiagnosisEntity extends AbstractEntity {
    private String content;

	@OneToOne
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
