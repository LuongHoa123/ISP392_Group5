package com.ISP392.demo.controller.patient;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/patient/appointment")
public class PatientAppointmentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    @GetMapping("")
    public String form(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);

        if (userEntity == null) {
            return "redirect:/index";
        }

        PatientEntity patient = userEntity.getPatients().stream().findFirst().orElse(null);

        if (patient != null) {
            model.addAttribute("patient", patient);
            return "appointment";
        }

        return "redirect:/";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam int age,
            @RequestParam String date,
            @RequestParam String problem
    ) {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity == null) {
            return "redirect:/patient/appointment?error=no_patient";
        }
        PatientEntity patient = userEntity.getPatients().stream().findFirst().orElse(null);
        if (patient == null) {
            return "redirect:/patient/appointment?error=no_patient";
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatient(patient);
        appointment.setAge(age);
        appointment.setName(name);
        appointment.setEmail(email);
        appointment.setPhoneNumber(phone);
        LocalDate appointmentDate = LocalDate.parse(date);
        appointment.setAppointmentDateTime(appointmentDate.atStartOfDay());
        appointment.setStatus(-1);
        appointment.setReason(problem);

        appointmentRepository.save(appointment);

        return "redirect:/patient/appointment?success=true";
    }

}
