package com.ISP392.demo.repository;

import com.ISP392.demo.entity.RoomEntity;
import com.ISP392.demo.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}
