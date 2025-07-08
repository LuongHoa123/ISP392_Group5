package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.CloudinaryService;
import com.ISP392.demo.utils.ImageUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private CloudinaryService cloudinaryService;

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
    public String updateProfile(NurseEntity formNurse, @RequestParam(name = "avatarFile", required = false) MultipartFile avatarFile, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        NurseEntity nurse = nurseRepository.findByUser(userEntity);
        if (nurse == null) return "redirect:/index";

        nurse.setFirstName(formNurse.getFirstName());
        nurse.setLastName(formNurse.getLastName());

        Optional<NurseEntity> existingNurse = nurseRepository.findByPhoneNumber(formNurse.getPhoneNumber());
        if (existingNurse.isPresent() && existingNurse.get().getId() != nurse.getId()) {
            return "redirect:/nurse/profile?phone=true";
        }

        nurse.setPhoneNumber(formNurse.getPhoneNumber());

        if(!avatarFile.isEmpty() && avatarFile != null) {
            String img = cloudinaryService.uploadFile(avatarFile);
            nurse.setAvatar(img);
        }

        session.setAttribute("avatar", nurse.getAvatar());

        nurseRepository.save(nurse);
        return "redirect:/nurse/profile?success=true";
    }
}
