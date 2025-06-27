package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "Doctors")
@Data
public class NurseEntity extends AbstractEntity {
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Basic
    @Column(name = "phoneNumber", nullable = true, length = 20)
    private String phoneNumber;
    private String address;

	@ManyToOne
	@JoinColumn(name = "userId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private UserEntity user;

    @OneToMany(mappedBy = "nurse", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<ShiftEntity> shifts;

    public Set<ShiftEntity> getShifts() {
        return shifts;
    }

    public void setShifts(Set<ShiftEntity> shifts) {
        this.shifts = shifts;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

}
