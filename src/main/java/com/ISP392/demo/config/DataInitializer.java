package com.ISP392.demo.config;

import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

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
    }
} 