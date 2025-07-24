package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "Users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        },
        catalog = "")
@Data
public class UserEntity extends AbstractEntity {
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isFirstLogin = false;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean createdByReceptionist = false;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer loginAttempts = 0;

    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer status = 1; // 1: active, 0: locked

	@ManyToOne
	@JoinColumn(name = "roleId")
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	private RoleEntity role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<PatientEntity> patients;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<DoctorEntity> doctors;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<RecepEntity> receps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<NurseEntity> nurses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<RequestEntity> requests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<LogsEntity> logs;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public Boolean getCreatedByReceptionist() {
        return createdByReceptionist;
    }

    public void setCreatedByReceptionist(Boolean createdByReceptionist) {
        this.createdByReceptionist = createdByReceptionist;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public Set<PatientEntity> getPatients() {
        return patients;
    }

    public void setPatients(Set<PatientEntity> patients) {
        this.patients = patients;
    }

    public Set<DoctorEntity> getDoctors() {
        return doctors;
    }

    public void setDoctors(Set<DoctorEntity> doctors) {
        this.doctors = doctors;
    }

    public Set<RecepEntity> getReceps() {
        return receps;
    }

    public void setReceps(Set<RecepEntity> receps) {
        this.receps = receps;
    }

    public Set<NurseEntity> getNurses() {
        return nurses;
    }

    public void setNurses(Set<NurseEntity> nurses) {
        this.nurses = nurses;
    }

    public Set<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(Set<RequestEntity> requests) {
        this.requests = requests;
    }

    public Set<LogsEntity> getLogs() {
        return logs;
    }

    public void setLogs(Set<LogsEntity> logs) {
        this.logs = logs;
    }
}
