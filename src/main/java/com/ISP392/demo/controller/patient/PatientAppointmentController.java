package com.ISP392.demo.controller.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;

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

    @GetMapping("/calendar")
    public String calendar(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);

        if (user == null) return "redirect:/index";

        PatientEntity patient = user.getPatients().stream().findFirst().orElse(null);

        if (patient == null) return "redirect:/";

        List<AppointmentEntity> appointments = appointmentRepository.findByPatient(patient);
        model.addAttribute("appointments", appointments);
        System.out.println(appointments);

        return "patient/history";
    }

    @GetMapping("/calendar/data")
    @ResponseBody
    public List<AppointmentDto> getAppointmentsJson() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        if (user == null) return List.of();

        PatientEntity patient = user.getPatients().stream().findFirst().orElse(null);
        if (patient == null) return List.of();

        List<AppointmentEntity> appointments = appointmentRepository.findByPatient(patient);

        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;

        for (AppointmentEntity appt : appointments) {
            if ((appt.getStatus() == -1 || appt.getStatus() == 2) && appt.getAppointmentDateTime().isBefore(now)) {
                appt.setStatus(0);
                changed = true;
            }
        }

        if (changed) {
            appointmentRepository.saveAll(appointments);
        }

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

    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        AppointmentEntity appt = appointmentRepository.findById(id).orElse(null);
        if (appt == null) return ResponseEntity.notFound().build();
        if (appt.getStatus() != 2) return ResponseEntity.badRequest().body("Không thể hủy");

        String noteCancel = body.get("noteCancel");
        if (noteCancel == null || noteCancel.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Lý do huỷ không được để trống");
        }

        appt.setNoteCancel(noteCancel);
        appt.setStatus(0);
        appointmentRepository.save(appt);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        AppointmentEntity appt = appointmentRepository.findById(id).orElse(null);
        if (appt == null) return ResponseEntity.notFound().build();
        if (appt.getStatus() != 0 && appt.getStatus() != -1) return ResponseEntity.badRequest().body("Chỉ xóa lịch đã hủy hoặc chờ xác nhận!");

        appointmentRepository.delete(appt);
        return ResponseEntity.ok().build();
    }


}
