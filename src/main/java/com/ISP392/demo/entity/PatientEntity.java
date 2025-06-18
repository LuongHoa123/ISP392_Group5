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
@Table(name = "Patients",
        catalog = "")
@Data
public class PatientEntity extends AbstractEntity {
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    @Column(name = "dateOfBirth", nullable = true)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private GenderEnum gender;

    @Basic
    @Column(name = "address", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String address;

    @Basic
    @Column(name = "phone", nullable = true, length = 20)
    private String phone;

	@ManyToOne
	@JoinColumn(name = "userId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private UserEntity user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<AppointmentEntity> appointments;
}
