package com.ISP392.demo.entity;

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
@Table(name = "Nurses")
@Data
public class NurseEntity extends AbstractEntity {
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Basic
    @Column(name = "phoneNumber", nullable = true, length = 20)
    private String phoneNumber;
    private String university;
    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    @Column(name = "dateOfBirth", nullable = true)
    private LocalDate dateOfBirth;

    private String address;
    private String avatar;
    private Integer yearOfGraduate;

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

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getYearOfGraduate() {
        return yearOfGraduate;
    }

    public void setYearOfGraduate(Integer yearOfGraduate) {
        this.yearOfGraduate = yearOfGraduate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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
