package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Conclusion")
@Data
public class ConclusionEntity extends AbstractEntity {
    private String content;
    private String prescription;

	@OneToOne
	@JoinColumn(name = "appointmentId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private AppointmentEntity appointment;

}
