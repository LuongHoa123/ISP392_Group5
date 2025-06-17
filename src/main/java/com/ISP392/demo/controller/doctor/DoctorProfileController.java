package com.ISP392.demo.controller.doctor;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private static final String CERTIFICATE_UPLOAD_DIR = "uploads/certificates";

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
    public String updateDoctorProfile(@RequestParam("certificateFile") MultipartFile file,
                                      DoctorEntity formDoctor) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) return "redirect:/index";

        doctor.setFirstName(formDoctor.getFirstName());
        doctor.setLastName(formDoctor.getLastName());

        Optional<DoctorEntity> existingDoctor = doctorRepository.findByPhoneNumber(formDoctor.getPhoneNumber());
        if (existingDoctor.isPresent() && existingDoctor.get().getId() != doctor.getId()) {
            return "redirect:/doctor/profile?phone=true";
        }

        doctor.setPhoneNumber(formDoctor.getPhoneNumber());
        doctor.setSpecialization(formDoctor.getSpecialization());

        // Xử lý upload file PDF
        if (file != null && !file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(CERTIFICATE_UPLOAD_DIR));
                String filename = "doctor_" + doctor.getId() + "_certificate.pdf";
                String filePath = CERTIFICATE_UPLOAD_DIR + "/" + filename;
                file.transferTo(new File(filePath));
                doctor.setCertificateFileName(filename);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/doctor/profile?error=file";
            }
        }

        doctorRepository.save(doctor);
        return "redirect:/doctor/profile?success=true";
    }

    @GetMapping("/certificate/view")
    public void viewCertificate(HttpServletResponse response) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null || doctor.getCertificateFileName() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(CERTIFICATE_UPLOAD_DIR + "/" + doctor.getCertificateFileName());
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + doctor.getCertificateFileName());

        try (FileInputStream inputStream = new FileInputStream(file)) {
            inputStream.transferTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
