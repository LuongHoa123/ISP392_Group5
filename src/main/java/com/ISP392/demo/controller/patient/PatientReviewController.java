package com.ISP392.demo.controller.patient;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.ReviewRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patient/review")
public class PatientReviewController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    private Long id = 0L;


    @GetMapping("/{id}")
    public String form(Model model, @PathVariable Long id) {
        this.id = id;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/index?error=email";
        }

        PatientEntity patient = userEntity.getPatients().stream().findFirst().orElse(null);
        if (patient == null) {
            return "redirect:/index?error=no_patient";
        }

        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return "redirect:/index?error=no_appointment";
        }

        ReviewEntity existingReview = reviewRepository.findByAppointmentIdAndPatientId(id, patient.getId());

        if (existingReview != null) {
            model.addAttribute("review", existingReview);
            return "review-already";
        }

        model.addAttribute("patient", patient);
        return "review";
    }


    @PostMapping("/save")
    public String saveReview(@RequestParam("content") String content,
                             @RequestParam("rating") Integer star) {
        ReviewEntity review = new ReviewEntity();
        review.setContent(content);
        review.setStar(star);

        AppointmentEntity appointmentEntity = appointmentRepository.findById(this.id).get();

        review.setAppointment(appointmentEntity);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);

        if (userEntity == null) {
            return "redirect:/index";
        }
        PatientEntity patient = userEntity.getPatients().stream().findFirst().orElse(null);
        review.setPatient(patient);
        reviewRepository.save(review);

        return "redirect:/patient/review/" + id + "?success=true";
    }

}
