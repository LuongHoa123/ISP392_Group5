package com.ISP392.demo.config;

import com.ISP392.demo.entity.RoomEntity;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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

        // Fix existing room data - extract floor from room name
        fixExistingRoomData();

        // Tạo dữ liệu phòng mẫu nếu chưa có
        if (roomRepository.count() == 0) {
            createSampleRooms();
            System.out.println("✅ Đã tạo dữ liệu phòng mẫu");
        }
    }

    private void fixExistingRoomData() {
        List<RoomEntity> allRooms = roomRepository.findAll();
        boolean hasUpdates = false;
        
        for (RoomEntity room : allRooms) {
            // Fix rooms that have incorrect floor data
            if (room.getRoomName() != null && room.getRoomName().startsWith("P") && room.getRoomName().length() >= 4) {
                try {
                    // Extract floor from room name: P101 -> 1, P201 -> 2, etc.
                    int extractedFloor = Integer.parseInt(room.getRoomName().substring(1, 2));
                    
                    // Update if floor is incorrect
                    if (room.getFloor() == null || !room.getFloor().equals(extractedFloor)) {
                        room.setFloor(extractedFloor);
                        roomRepository.save(room);
                        hasUpdates = true;
                        System.out.println("🔧 Sửa phòng: " + room.getRoomName() + " -> Tầng " + extractedFloor);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Không thể extract tầng từ tên phòng: " + room.getRoomName());
                }
            }
        }
        
        if (hasUpdates) {
            System.out.println("✅ Đã sửa dữ liệu tầng cho các phòng");
        }
    }

    private void createSampleRooms() {
        // Tầng 1 - Phòng khám cơ bản
        createRoom("P101", 1, "Phòng tai", "Phòng khám chuyên về các bệnh lý tai");
        createRoom("P102", 1, "Phòng mũi", "Phòng khám chuyên về các bệnh lý mũi");
        createRoom("P103", 1, "Phòng họng", "Phòng khám chuyên về các bệnh lý họng");
        
        // Tầng 2 - Phòng chuyên khoa
        createRoom("P201", 2, "Phòng nội soi (tai - mũi/ họng)", "Phòng nội soi chuyên khoa tai mũi họng");
        createRoom("P202", 2, "Phòng thủ thuật (hút mũi, lấy dị vật)", "Phòng thực hiện các thủ thuật nhỏ");
        
        // Tầng 3 - Phòng xét nghiệm
        createRoom("P301", 3, "Phòng xét nghiệm", "Phòng xét nghiệm máu, nước tiểu và các xét nghiệm cơ bản");
    }
    
    private void createRoom(String roomName, Integer floor, String roomType, String description) {
        RoomEntity room = new RoomEntity();
        room.setRoomName(roomName);
        room.setFloor(floor);
        room.setRoomType(roomType);
        room.setDescription(description);
        roomRepository.save(room);
    }
} 