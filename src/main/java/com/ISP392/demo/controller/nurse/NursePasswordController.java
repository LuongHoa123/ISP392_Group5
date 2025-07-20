package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/nurse")
public class NursePasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/changePass")
    public String changePassPage() {
        return "nurse/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            model.addAttribute("error", "Người dùng không tồn tại.");
            return "nurse/change-password";
        }

        UserEntity user = optionalUser.get();

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "nurse/change-password";
        }

        // Kiểm tra mật khẩu mới không được trùng với mật khẩu hiện tại
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới không được giống mật khẩu cũ.");
            return "nurse/change-password";
        }

        // Kiểm tra password mới theo yêu cầu: tối thiểu 7 ký tự, có chữ hoa và ký tự đặc biệt
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$")) {
            if (newPassword.length() < 7) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 7 ký tự!");
            } else if (!newPassword.matches(".*[A-Z].*")) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 1 chữ hoa!");
            } else if (!newPassword.matches(".*[@$!%*?&].*")) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 1 ký tự đặc biệt (@$!%*?&)!");
            } else {
                model.addAttribute("error", "Mật khẩu chỉ được chứa chữ cái, số và ký tự đặc biệt @$!%*?&!");
            }
            return "nurse/change-password";
        }

        // Kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận không trùng khớp.");
            return "nurse/change-password";
        }

        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return "nurse/change-password";
    }
} 