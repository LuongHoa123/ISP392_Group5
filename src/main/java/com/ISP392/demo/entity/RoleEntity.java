package com.ISP392.demo.entity;

import com.ISP392.demo.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
@Getter
@Entity
@Table(name = "Roles")
public class RoleEntity extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 255, unique = true, columnDefinition = "nvarchar(255)")
    private RoleEnum name;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
	private Set<UserEntity> users;

}
