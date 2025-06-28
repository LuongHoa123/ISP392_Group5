package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/nurse")
public class NurseProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @GetMapping("/profile")
    public String viewDoctorProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/nurse/dashboard";
        }

        NurseEntity nurse = nurseRepository.findByUser(userEntity);
        if (nurse != null) {
            model.addAttribute("nurse", nurse);
            return "nurse/profile";
        }

        return "redirect:/";
    }

    @PostMapping("/profile/save")
    public String updateProfile(NurseEntity formNurse) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        NurseEntity nurse = nurseRepository.findByUser(userEntity);
        if (nurse == null) return "redirect:/index";

        nurse.setFirstName(formNurse.getFirstName());
        nurse.setLastName(formNurse.getLastName());

        Optional<NurseEntity> existingDoctor = nurseRepository.findByPhoneNumber(formNurse.getPhoneNumber());
        if (existingDoctor.isPresent() && existingDoctor.get().getId() != nurse.getId()) {
            return "redirect:/nurse/profile?phone=true";
        }

        nurse.setPhoneNumber(formNurse.getPhoneNumber());

        nurseRepository.save(nurse);
        return "redirect:/nurse/profile?success=true";
    }
}
