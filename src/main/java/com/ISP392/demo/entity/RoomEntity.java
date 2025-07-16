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
    private String roomType; // Ví dụ: Phòng tai, Phòng mũi, Phòng họng

    @Column(name = "floor", nullable = true)
    private Integer floor;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}