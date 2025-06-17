package com.ISP392.demo.controller.doctor;


import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
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
@RequestMapping("/doctor")
public class DoctorHomeController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/doctor/dashboard";
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }
        session.setAttribute("fullName", doctor.getFirstName() + " " + doctor.getLastName());
        session.setAttribute("specialization", "Chuyên khoa: " + doctor.getSpecialization());
        return "doctor/dashboard";
    }
}