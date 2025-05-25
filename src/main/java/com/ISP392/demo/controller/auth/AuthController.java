package com.ISP392.demo.controller.auth;

import com.ISP392.demo.service.EmailSenderService;
import com.ISP392.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, EmailSenderService emailSenderService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "register")
    public String addUser() {
        return "register";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam String address, @RequestParam String gender, @RequestParam String dob,
                       @RequestParam String phone, @RequestParam String password, Model model, HttpSession session) {
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
}
