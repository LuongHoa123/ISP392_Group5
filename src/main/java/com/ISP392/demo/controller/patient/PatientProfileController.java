package com.ISP392.demo.controller.patient;

import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.CloudinaryService;
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

@Controller
@RequestMapping("/patient")
public class PatientProfileController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public PatientProfileController(UserRepository userRepository, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);

        if (userEntity == null) {
            return "redirect:/index";
        }

        PatientEntity patient = userEntity.getPatients().stream().findFirst().orElse(null);

        if (patient != null) {
            model.addAttribute("patient", patient);
            return "patient/profile";
        }

        return "redirect:/";
    }

    @PostMapping("/update")
    public String updateProfile(PatientEntity patient, @RequestParam(name = "avatarFile", required = false) MultipartFile avatarFile, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);

        if (userEntity == null) {
            return "redirect:/index";
        }

        PatientEntity existingPatient = userEntity.getPatients().stream().findFirst().orElse(null);

        if (existingPatient == null) {
            return "redirect:/index";
        }

        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        existingPatient.setGender(patient.getGender());
        existingPatient.setAddress(patient.getAddress());
        existingPatient.setPhone(patient.getPhone());

        if(!avatarFile.isEmpty() && avatarFile != null) {
            String img = cloudinaryService.uploadFile(avatarFile);
            existingPatient.setAvatar(img);
        }
        session.setAttribute("avatar", patient.getAvatar());

        patientRepository.save(existingPatient);

        return "redirect:/patient/profile?success=true";
    }
}
