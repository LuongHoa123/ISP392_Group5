package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AppointmentService")
@Data
public class AppointmentServiceEntity extends AbstractEntity {
    private String content;


	@ManyToOne
	@JoinColumn(name = "appointment_id")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private AppointmentEntity appointment;

	@ManyToOne
	@JoinColumn(name = "service_id")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private ServiceEntity service;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public AppointmentEntity getAppointment() {
		return appointment;
	}

	public void setAppointment(AppointmentEntity appointment) {
		this.appointment = appointment;
	}

	public ServiceEntity getService() {
		return service;
	}

	public void setService(ServiceEntity service) {
		this.service = service;
	}
}
