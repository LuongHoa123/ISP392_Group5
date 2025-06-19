package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Request")
@Data
public class RequestEntity extends AbstractEntity {
    @NotBlank
    @Size(max = 50)
    private String content;

	@ManyToOne
	@JoinColumn(name = "userId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private UserEntity user;
}
