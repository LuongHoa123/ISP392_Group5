package com.ISP392.demo.entity;

import com.ISP392.demo.App;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Service")
@Data
public class ServiceEntity extends AbstractEntity {
    private String content;
    private BigDecimal price;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
	private Set<AppointmentServiceEntity> appointmentServiceEntities;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Set<AppointmentServiceEntity> getAppointmentServiceEntities() {
		return appointmentServiceEntities;
	}

	public void setAppointmentServiceEntities(Set<AppointmentServiceEntity> appointmentServiceEntities) {
		this.appointmentServiceEntities = appointmentServiceEntities;
	}
}
