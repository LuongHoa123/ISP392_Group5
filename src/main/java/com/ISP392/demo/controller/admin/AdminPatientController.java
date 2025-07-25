package com.ISP392.demo.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.PatientRepository;

@Controller
@RequestMapping("/admin/patient")
public class AdminPatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final int PAGE_SIZE = 5;

    @GetMapping("/test")
    public String testPage() {
        return "admin/patient/test";
    }

    @GetMapping("")
    public String listPatients(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "search", required = false) String search) {

        List<PatientEntity> allPatients = patientRepository.findAll();

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

        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

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

    @GetMapping("/medical-record/{appointmentId}")
    public String viewMedicalRecord(@PathVariable Long appointmentId, Model model) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
        
        if (appointment == null) {
            model.addAttribute("errorMessage", "Không tìm thấy lịch khám.");
            return "admin/patient/medical-record";
        }

        if (appointment.getStatus() != 1) {
            model.addAttribute("errorMessage", "Lịch khám chưa hoàn thành.");
            return "admin/patient/medical-record";
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("patient", appointment.getPatient());
        
        return "admin/patient/medical-record";
    }
} 