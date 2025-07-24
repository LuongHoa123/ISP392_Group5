package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping
    public String listPatients(Model model,
                               @RequestParam(required = false) String date,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String searchKeyword) {

        LocalDate currentDate = (date != null && !date.isEmpty()) ?
                LocalDate.parse(date) : LocalDate.now();

        LocalDateTime startOfDay = currentDate.atStartOfDay();
        LocalDateTime endOfDay   = currentDate.atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(page, 10, Sort.by("appointmentDateTime").descending());

        Page<AppointmentEntity> appts;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            appts = appointmentRepository.findByAppointmentDateTimeBetweenAndPatientNameOrPhone(startOfDay, endOfDay, searchKeyword.trim(), pageable);
        } else {
            appts = appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay, pageable);
        }

        Long totalPatients = appointmentRepository.countDistinctPatientByAppointmentDateTimeBetween(startOfDay, endOfDay);

        model.addAttribute("appointments", appts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appts.getTotalPages());
        model.addAttribute("selectedDate", currentDate);
        model.addAttribute("prevDate", currentDate.minusDays(1));
        model.addAttribute("nextDate", currentDate.plusDays(1));
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("searchKeyword", searchKeyword);

        return "nurse/patient/list";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(required = false) String date) throws IOException {
        LocalDate currentDate = (date != null && !date.isEmpty()) ?
                LocalDate.parse(date) : LocalDate.now();
        LocalDateTime startOfDay = currentDate.atStartOfDay();
        LocalDateTime endOfDay   = currentDate.atTime(23, 59, 59);

        List<PatientEntity> patients = appointmentRepository.findDistinctPatientsByAppointmentDateTimeBetween(startOfDay, endOfDay);

        ByteArrayInputStream bis = pdfExportService.exportPatientsToPdf(patients);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=patient_list_" + currentDate.toString() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
}
