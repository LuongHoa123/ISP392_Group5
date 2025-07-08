package com.ISP392.demo.entity;

import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Reviews",
        catalog = "")
@Data
public class ReviewEntity extends AbstractEntity {
    @Column(name = "firstName")
    private String content;

    @Column(name = "star")
    private Integer star;

	@ManyToOne
	@JoinColumn(name = "patientId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private PatientEntity patient;

	@OneToOne
	@JoinColumn(name = "appointmentId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private AppointmentEntity appointment;
}
