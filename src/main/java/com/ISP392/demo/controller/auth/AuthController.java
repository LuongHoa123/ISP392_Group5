package com.ISP392.demo.controller.auth;

import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.EmailSenderService;
import com.ISP392.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    public AuthController(UserService userService, EmailSenderService emailSenderService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/?logout";
    }

    @RequestMapping(value = "register")
    public String addUser() {
        return "register";
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam String address, @RequestParam String gender, @RequestParam String dob,
                       @RequestParam String phone, @RequestParam String password, Model model, HttpSession session) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("mess", "Email không hợp lệ!");
            return "register";
        }

        if (userService.findByEmail(email).isPresent()) {
            model.addAttribute("mess", "Email đã tồn tại. Hãy nhập Email mới!");
            return "register";
        }

        if (patientRepository.findByPhone(phone).isPresent()) {
            model.addAttribute("mess", "Số điện thoại này đã được đăng ký. Hãy nhập số điện thoại mới!");
            return "register";
        }

        if (!firstName.matches("^[\\p{L} .'-]+$") || !lastName.matches("^[\\p{L} .'-]+$")) {
            model.addAttribute("mess", "Họ và tên không hợp lệ! Vui lòng chỉ nhập chữ.");
            return "register";
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            model.addAttribute("mess", "Số điện thoại không hợp lệ!");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("mess", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "register";
        }

        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);
        if (userService.findByEmail(email).isPresent()) {
            model.addAttribute("mess", "Email đã tồn tại. Hãy nhập Email mới!");
            return "register";
        }
        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);
        String subject = "Đây là OTP của bạn";
        String mess = "Xin chào @" + " \n" + email + "Đây là OTP của bạn: " + session.getAttribute("otp-register") + " Hãy điền vào form!" + "\n Cảm ơn!";
        this.emailSenderService.sendEmail(email, subject, mess);
        session.setAttribute("email", email);
        session.setAttribute("firstName", firstName);
        session.setAttribute("lastName", lastName);
        session.setAttribute("address", address);
        session.setAttribute("gender", gender);
        session.setAttribute("dob", dob);
        session.setAttribute("phone", phone);
        session.setAttribute("password", password);
        return "redirect:/otp-check";
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }

    @GetMapping("/changePass")
    public String changePass() {
        return "change-password";
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
            return "change-password";
        }

        UserEntity user = optionalUser.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "change-password";
        }
        // ✅ Kiểm tra mật khẩu mới không được trùng với mật khẩu hiện tại
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới không được giống mật khẩu cũ.");
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận không trùng khớp.");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return "change-password";
    }

}
