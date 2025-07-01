package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/nurse/patient")
public class NursePatientController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String listPatients(Model model,
                               @RequestParam(required = false) String date,
                               @RequestParam(defaultValue = "0") int page) {

        LocalDate currentDate = (date != null && !date.isEmpty()) ?
                LocalDate.parse(date) : LocalDate.now();

        LocalDateTime startOfDay = currentDate.atStartOfDay();
        LocalDateTime endOfDay   = currentDate.atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(page, 10, Sort.by("appointmentDateTime").descending());

        Page<AppointmentEntity> appts = appointmentRepository
                .findByAppointmentDateTimeBetween(
                        startOfDay, endOfDay, pageable);

        List<PatientEntity> patients = appts.stream()
                .map(AppointmentEntity::getPatient)
                .distinct()
                .toList();

        model.addAttribute("patients", patients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appts.getTotalPages());
        model.addAttribute("selectedDate", currentDate);
        model.addAttribute("prevDate", currentDate.minusDays(1));
        model.addAttribute("nextDate", currentDate.plusDays(1));

        List<AppointmentEntity> appointments = appts
                .getContent();

        model.addAttribute("appointments", appointments);


        return "nurse/patient/list";
    }


}
