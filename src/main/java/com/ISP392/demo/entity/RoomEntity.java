package com.ISP392.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rooms")
@Data
public class RoomEntity extends AbstractEntity {

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(name = "location", nullable = true, length = 255)
    private String location;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "room_type", nullable = true, length = 100)
    private String roomType; // Ví dụ: "Khám tổng quát", "Nội soi", "Sản phụ khoa", v.v.

    @Column(name = "capacity", nullable = true)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Primary_doctor")
    private DoctorEntity primaryDoctor;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
}