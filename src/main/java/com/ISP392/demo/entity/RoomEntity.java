package com.ISP392.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Rooms")
@Data
public class RoomEntity extends AbstractEntity {

    @Column(name = "roomName", nullable = false, length = 100)
    private String roomName;

    @Column(name = "location", nullable = true, length = 255)
    private String location;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "roomType", nullable = true, length = 100)
    private String roomType; // Ví dụ: "Khám tổng quát", "Nội soi", "Sản phụ khoa", v.v.

    @Column(name = "capacity", nullable = true)
    private Integer capacity;
}