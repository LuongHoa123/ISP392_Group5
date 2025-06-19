package com.ISP392.demo.controller.admin;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/patient")
public class AdminPatientController {

    @Autowired
    private PatientRepository patientRepository;

    private static final int PAGE_SIZE = 5;

    @GetMapping("")
    public String listPatients(Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "search", required = false) String search) {

        List<PatientEntity> allPatients = patientRepository.findAll();

        // Apply search filter if search parameter is present
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            allPatients = allPatients.stream()
                    .filter(patient ->
                            (patient.getFirstName() != null && patient.getFirstName().toLowerCase().contains(searchLower)) ||
                            (patient.getLastName() != null && patient.getLastName().toLowerCase().contains(searchLower)) ||
                            (patient.getPhone() != null && patient.getPhone().contains(searchLower)) ||
                            (patient.getUser() != null && patient.getUser().getEmail() != null && 
                             patient.getUser().getEmail().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }

        // Calculate pagination
        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        // Ensure page is within valid range
        if (page < 0) {
            page = 0;
        } else if (page >= totalPages && totalPages > 0) {
            page = totalPages - 1;
        }
        
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalItems);

        List<PatientEntity> patients = allPatients.subList(start, end);

        model.addAttribute("patients", patients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("pageSize", PAGE_SIZE);

        return "admin/patient/list";
    }

    @GetMapping("/{id}/appointments")
    @ResponseBody
    public ResponseEntity<List<AppointmentDto>> getPatientAppointments(@PathVariable Long id) {
        PatientEntity patient = patientRepository.findById(id).orElse(null);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }

        List<AppointmentDto> appointments = patient.getAppointments().stream()
                .map(appt -> {
                    AppointmentDto dto = new AppointmentDto();
                    dto.setId(appt.getId());
                    dto.setAppointmentDateTime(appt.getAppointmentDateTime());
                    dto.setReason(appt.getReason());
                    dto.setStatus(appt.getStatus());
                    
                    if (appt.getDoctor() != null) {
                        dto.setDoctorName(appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName());
                        dto.setDoctorSpecialization(appt.getDoctor().getSpecialization());
                    }
                    
                    if (appt.getRoom() != null) {
                        dto.setRoomName(appt.getRoom().getRoomName());
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointments);
    }
} 