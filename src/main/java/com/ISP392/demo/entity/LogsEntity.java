package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Logs")
@Data
public class LogsEntity extends AbstractEntity {
    private String content;

	@ManyToOne
	@JoinColumn(name = "userId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private UserEntity user;
}
