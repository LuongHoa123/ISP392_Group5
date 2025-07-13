package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
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
	private Set<DiagnosisEntity> diagnosisEntities;

}
