package com.ISP392.demo.config;

import com.ISP392.demo.entity.RoomEntity;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        // Cập nhật tất cả user có sẵn trong database: đánh dấu KHÔNG được tạo bởi lễ tân
        userRepository.findAll().forEach(user -> {
            if (user.getCreatedByReceptionist() == null) {
                user.setCreatedByReceptionist(false); // User cũ không được tạo bởi lễ tân
                user.setIsFirstLogin(false); // User cũ không cần xác thực
                userRepository.save(user);
            }
        });
        
        System.out.println("✅ Đã cập nhật: Tất cả user cũ đánh dấu KHÔNG được tạo bởi lễ tân");

        // Tạo dữ liệu phòng mẫu nếu chưa có
        if (roomRepository.count() == 0) {
            createSampleRooms();
            System.out.println("✅ Đã tạo dữ liệu phòng mẫu");
        }
    }

    private void createSampleRooms() {
        // Phòng tai
        RoomEntity earRoom = new RoomEntity();
        earRoom.setRoomName("Phòng khám tai 1");
        earRoom.setRoomType("Phòng tai");
        earRoom.setLocation("Tầng 1");
        earRoom.setDescription("Phòng khám chuyên về các bệnh lý tai");
        roomRepository.save(earRoom);

        // Phòng mũi
        RoomEntity noseRoom = new RoomEntity();
        noseRoom.setRoomName("Phòng khám mũi 1");
        noseRoom.setRoomType("Phòng mũi");
        noseRoom.setLocation("Tầng 1");
        noseRoom.setDescription("Phòng khám chuyên về các bệnh lý mũi");
        roomRepository.save(noseRoom);

        // Phòng họng
        RoomEntity throatRoom = new RoomEntity();
        throatRoom.setRoomName("Phòng khám họng 1");
        throatRoom.setRoomType("Phòng họng");
        throatRoom.setLocation("Tầng 2");
        throatRoom.setDescription("Phòng khám chuyên về các bệnh lý họng");
        roomRepository.save(throatRoom);

        // Phòng nội soi
        RoomEntity endoscopyRoom = new RoomEntity();
        endoscopyRoom.setRoomName("Phòng nội soi TMH");
        endoscopyRoom.setRoomType("Phòng nội soi (tai - mũi/ họng)");
        endoscopyRoom.setLocation("Tầng 2");
        endoscopyRoom.setDescription("Phòng nội soi chuyên khoa tai mũi họng");
        roomRepository.save(endoscopyRoom);

        // Phòng thủ thuật
        RoomEntity procedureRoom = new RoomEntity();
        procedureRoom.setRoomName("Phòng thủ thuật TMH");
        procedureRoom.setRoomType("Phòng thủ thuật (hút mũi, lấy dị vật)");
        procedureRoom.setLocation("Tầng 3");
        procedureRoom.setDescription("Phòng thực hiện các thủ thuật nhỏ như hút mũi, lấy dị vật");
        roomRepository.save(procedureRoom);

        // Phòng xét nghiệm
        RoomEntity labRoom = new RoomEntity();
        labRoom.setRoomName("Phòng xét nghiệm tổng hợp");
        labRoom.setRoomType("Phòng xét nghiệm");
        labRoom.setLocation("Tầng 3");
        labRoom.setDescription("Phòng xét nghiệm máu, nước tiểu và các xét nghiệm cơ bản");
        roomRepository.save(labRoom);
    }
} 