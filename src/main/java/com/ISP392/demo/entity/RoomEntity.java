package com.ISP392.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
public class RoomEntity extends AbstractEntity {

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName; // Now will be like P101, P102, etc.

    @Column(name = "floor", nullable = false)
    private Integer floor; // Floor number (1, 2, 3, etc.)

    @Column(name = "room_type", nullable = false, length = 100)
    private String roomType; // Room type like "Phòng tai", "Phòng mũi", etc.

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<AppointmentEntity> appointments;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<ShiftEntity> shifts;
}