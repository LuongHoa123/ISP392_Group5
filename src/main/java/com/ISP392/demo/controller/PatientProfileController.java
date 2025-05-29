package com.ISP392.demo.controller;

import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientProfileController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

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
    public String updateProfile(PatientEntity patient) {
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

        patientRepository.save(existingPatient);

        return "redirect:/patient/profile?success=true";
    }
}
