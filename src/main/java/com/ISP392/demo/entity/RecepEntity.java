package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "Receptionists")
@Data
public class RecepEntity extends AbstractEntity {
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Basic
    @Column(name = "phoneNumber", nullable = true, length = 20)
    private String phoneNumber;

    private String email;

    @ManyToOne
    @JoinColumn(name = "userId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "recep", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<LogsEntity> logs;
}