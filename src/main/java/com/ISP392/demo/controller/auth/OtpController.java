package com.ISP392.demo.controller.auth;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.service.*;
import com.ISP392.demo.utils.DateUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class OtpController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    @Autowired
    private PatientRepository patientRepository;

    public OtpController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "otpConfirm";
    }

    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model) {
        String otpRegister = (String) session.getAttribute("otp-register");
        if (otp.equals(otpRegister)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail((String) session.getAttribute("email"));
            userEntity.setPassword(passwordEncoder.encode((String) session.getAttribute("password")));
            userEntity.setStatus(1);

            RoleEntity role = roleService.findById(4L).get();
            userEntity.setRole(role);

            UserEntity save = userService.saveUser(userEntity);

            PatientEntity patientEntity = new PatientEntity();
            patientEntity.setUser(save);

            patientEntity.setPhone((String) session.getAttribute("phone"));
            patientEntity.setFirstName((String) session.getAttribute("firstName"));
            patientEntity.setLastName((String) session.getAttribute("lastName"));
            patientEntity.setAddress((String) session.getAttribute("address"));

            patientEntity.setGender(GenderEnum.valueOf((String) session.getAttribute("gender")));
            patientEntity.setDateOfBirth(DateUtils.toDate((String) session.getAttribute("dob")));

            patientRepository.save(patientEntity);

            return "redirect:/";
        }
        model.addAttribute("mess","OTP is not correct! Please check your email.");
        return "otpConfirm";
    }

}