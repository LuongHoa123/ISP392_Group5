package com.ISP392.demo.controller.doctor;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/profile")
    public String viewDoctorProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/doctor/dashboard";
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor != null) {
            model.addAttribute("doctor", doctor);
            return "doctor/profile";
        }

        return "redirect:/";
    }

    @PostMapping("/profile/save")
    public String updateDoctorProfile(DoctorEntity formDoctor) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) return "redirect:/index";

        doctor.setFirstName(formDoctor.getFirstName());
        doctor.setLastName(formDoctor.getLastName());
        doctor.setPhoneNumber(formDoctor.getPhoneNumber());
        doctor.setSpecialization(formDoctor.getSpecialization());
        doctor.setYoe(formDoctor.getYoe());

        doctorRepository.save(doctor);

        return "redirect:/doctor/profile?success=true";
    }
}
