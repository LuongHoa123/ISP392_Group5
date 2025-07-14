package com.ISP392.demo.controller.doctor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.service.ExcelExportService;
import com.ISP392.demo.service.PdfExportService;
import com.ISP392.demo.service.WordExportService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/doctor/appointment")
public class DoctorAppointmentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private WordExportService wordExportService;

    @GetMapping("")
    public String viewAppointmentsForDoctor(Model model,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "date", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "5") int size,
                                            @RequestParam(value = "patientId", required = false) Long patientId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/doctor/dashboard";
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }

        List<AppointmentEntity> allAppointments = appointmentRepository.findByDoctor(doctor);

        if (patientId != null) {
            allAppointments = allAppointments.stream()
                    .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patientId))
                    .collect(Collectors.toList());
            model.addAttribute("patientId", patientId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            allAppointments = allAppointments.stream()
                    .filter(a -> (a.getName() != null && a.getName().toLowerCase().contains(lowerKeyword)) ||
                            (a.getEmail() != null && a.getEmail().toLowerCase().contains(lowerKeyword)) ||
                            (a.getReason() != null && a.getReason().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        if (date != null) {
            allAppointments = allAppointments.stream()
                    .filter(a -> a.getAppointmentDateTime().toLocalDate().isEqual(date))
                    .collect(Collectors.toList());
        }

        int totalItems = allAppointments.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);
        List<AppointmentEntity> appointments = allAppointments.subList(start, end);

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("date", date);

        return "doctor/appointment/list";
    }


    @GetMapping("/{id}")
    @ResponseBody
    public AppointmentEntity getAppointmentDetails(@PathVariable Long id) {
        System.out.println(appointmentRepository.findById(id));
        return appointmentRepository.findById(id).orElse(null);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<Resource> exportToExcel() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return ResponseEntity.badRequest().build();
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AppointmentEntity> appointments = appointmentRepository.findByDoctor(doctor);

        try {
            ByteArrayInputStream excelFile = excelExportService.exportAppointmentsToExcel(appointments);

            String filename = "lich_hen_kham_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            InputStreamResource file = new InputStreamResource(excelFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<Resource> exportToPdf() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return ResponseEntity.badRequest().build();
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AppointmentEntity> appointments = appointmentRepository.findByDoctor(doctor);

        try {
            ByteArrayInputStream pdfFile = pdfExportService.exportAppointmentsToPdf(appointments);
            String filename = "lich_hen_kham_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";
            InputStreamResource file = new InputStreamResource(pdfFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export-word")
    public ResponseEntity<Resource> exportToWord() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return ResponseEntity.badRequest().build();
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AppointmentEntity> appointments = appointmentRepository.findByDoctor(doctor);

        try {
            ByteArrayInputStream wordFile = wordExportService.exportAppointmentsToWord(appointments);
            String filename = "lich_hen_kham_" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".docx";
            InputStreamResource file = new InputStreamResource(wordFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .body(file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/calendar/data")
    @ResponseBody
    public List<AppointmentDto> getAppointmentsJson() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        if (user == null) return List.of();

        DoctorEntity doctor = doctorRepository.findByUser(user);
        if (doctor == null) return List.of();

        List<AppointmentEntity> appointments = appointmentRepository.findByDoctor(doctor);

        return appointments.stream().map(appt -> {
            AppointmentDto dto = new AppointmentDto();
            dto.setAppointmentDateTime(appt.getAppointmentDateTime());
            dto.setReason(appt.getReason());
            dto.setName(appt.getName());
            dto.setPhoneNumber(appt.getPhoneNumber());
            dto.setEmail(appt.getEmail());
            dto.setAge(appt.getAge());
            dto.setStatus(appt.getStatus());
            dto.setId(appt.getId());

            if (appt.getRoom() != null) {
                dto.setRoomName(appt.getRoom().getRoomName());
            }
            if (appt.getDoctor() != null) {
                dto.setDoctorName(appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName());
                dto.setDoctorSpecialization(appt.getDoctor().getSpecialization());
            }
            return dto;
        }).toList();
    }

    @Autowired
    private ConclusionRepository conclusionRepository;

    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;

    @GetMapping("/conclusion/{id}")
    public String showConclusionPage(@PathVariable Long id, Model model) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return "redirect:/doctor/appointment";
        }

        ConclusionEntity conclusion = appointment.getConclusionEntity();
        if (conclusion == null) {
            conclusion = new ConclusionEntity();
        }

        List<AppointmentServiceEntity> appointmentServices = appointmentServiceRepository.findByAppointmentId(id);

        model.addAttribute("appointment", appointment);
        model.addAttribute("conclusion", conclusion);
        model.addAttribute("appointmentServices", appointmentServices);

        return "doctor/appointment/conclusion";
    }


    @PostMapping("/conclusion/{id}")
    @Transactional
    public String saveConclusion(@PathVariable Long id, @RequestParam(required = false) String content, @RequestParam(required = false) String prescription) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return "redirect:/doctor/appointment?error=true";
        }

        try {
            ConclusionEntity conclusionEntity = appointment.getConclusionEntity() != null ?
                    appointment.getConclusionEntity() :
                    new ConclusionEntity();

            conclusionEntity.setContent(content);
            conclusionEntity.setPrescription(prescription);
            conclusionEntity.setAppointment(appointment);

            conclusionRepository.save(conclusionEntity);

            appointment.setStatus(1);
            appointmentRepository.save(appointment);

            return "redirect:/doctor/appointment?save=true";
        } catch (ObjectOptimisticLockingFailureException ex) {
            return "redirect:/doctor/appointment?conflict=true";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/doctor/appointment?error=true";
        }
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteAppointment(@PathVariable Long id) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return "redirect:/doctor/appointment?deleteError=true";
        }
        try {
            appointmentRepository.delete(appointment);
            return "redirect:/doctor/appointment?deleted=true";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/doctor/appointment?deleteError=true";
        }
    }


}
