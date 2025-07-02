package com.ISP392.demo.controller.nurse;


import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nurse")
public class NurseHomeController {

    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String home(HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/nurse/dashboard";
        }

        NurseEntity nurse = nurseRepository.findByUser(userEntity);
        if (nurse == null) {
            return "redirect:/nurse/dashboard";
        }
        session.setAttribute("fullName", nurse.getFirstName() + " " + nurse.getLastName());
        session.setAttribute("avatar", nurse.getAvatar());

        return "nurse/dashboard";
    }
}