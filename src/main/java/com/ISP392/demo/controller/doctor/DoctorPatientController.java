package com.ISP392.demo.controller.doctor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.ConclusionEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;

@Controller
@RequestMapping("/doctor/patient")
public class DoctorPatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("")
    public String patientListPage(Model model,
                                  @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                  @RequestParam(value = "searchDate", required = false) String searchDate,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);

        List<PatientEntity> allPatients = doctor.getAppointments().stream()
                .map(AppointmentEntity::getPatient)
                .filter(p -> p != null)
                .distinct()
                .collect(Collectors.toList());


        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.toLowerCase().trim();
            allPatients = allPatients.stream()
                    .filter(p -> {
                        boolean matchFirstName = p.getFirstName() != null && 
                                p.getFirstName().toLowerCase().contains(keyword);
                        boolean matchLastName = p.getLastName() != null && 
                                p.getLastName().toLowerCase().contains(keyword);
                        boolean matchPhone = p.getPhone() != null && 
                                p.getPhone().contains(searchKeyword.trim());
                        
                        return matchFirstName || matchLastName || matchPhone;
                    })
                    .collect(Collectors.toList());
        }
        // Lọc theo ngày khám (lịch sử khám bệnh) nếu có
        if (searchDate != null && !searchDate.isEmpty()) {
            allPatients = allPatients.stream()
                .filter(p -> p.getAppointments().stream()
                    .anyMatch(a -> a.getDoctor() != null && a.getDoctor().getId().equals(doctor.getId()) &&
                        a.getAppointmentDateTime() != null &&
                        a.getAppointmentDateTime().toLocalDate().toString().equals(searchDate)))
                .collect(Collectors.toList());
        }

        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<PatientEntity> patients = allPatients.subList(start, end);

        model.addAttribute("patients", patients);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchDate", searchDate);

        return "doctor/patient/list";
    }

    @GetMapping("/detail")
    public String patientDetailPage(Model model,
                                    @RequestParam("id") Long patientId,
                                    @RequestParam(value = "page", defaultValue = "0") int page) {
        PatientEntity patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return "redirect:/doctor/patient";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        DoctorEntity doctor = doctorRepository.findByUser(user);

        List<AppointmentDto> history = patient.getAppointments().stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getId().equals(doctor.getId()))
                .sorted((a1, a2) -> a2.getAppointmentDateTime().compareTo(a1.getAppointmentDateTime()))
                .map(a -> {
                    AppointmentDto dto = new AppointmentDto();
                    dto.setId(a.getId());
                    dto.setAppointmentDateTime(a.getAppointmentDateTime());
                    dto.setReason(a.getReason());
                    dto.setName(a.getName());
                    dto.setPhoneNumber(a.getPhoneNumber());
                    dto.setAge(a.getAge());
                    dto.setEmail(a.getEmail());
                    dto.setStatus(a.getStatus());
                    dto.setRoomName(a.getRoom() != null ? a.getRoom().getRoomName() : null);
                    // prescription: ưu tiên lấy từ conclusionEntity nếu có, nếu không lấy từ trường prescription
                    ConclusionEntity ce = a.getConclusionEntity();
                    if (ce != null) {
                        dto.setPrescription(ce.getPrescription());
                        dto.setConclusionContent(ce.getContent());
                    } else {
                        dto.setPrescription(a.getPrescription());
                        dto.setConclusionContent(a.getConclusion());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        int size = 5;
        int totalItems = history.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);
        List<AppointmentDto> appointmentsPage = history.subList(start, end);

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "doctor/patient/detail";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deletePatientFromDoctor(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/doctor/patient?deleteError=true";
        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        PatientEntity patient = patientRepository.findById(id).orElse(null);
        if (doctor == null || patient == null) return "redirect:/doctor/patient?deleteError=true";
        // Xóa tất cả các lịch hẹn giữa bác sĩ này và bệnh nhân này
        appointmentRepository.deleteByDoctorAndPatient(doctor, patient);
        return "redirect:/doctor/patient?deleted=true";
    }

    @PostMapping("/delete-appointment/{appointmentId}")
    @Transactional
    public String deleteAppointmentFromPatient(@PathVariable Long appointmentId, @RequestParam("id") Long patientId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        DoctorEntity doctor = doctorRepository.findByUser(user);
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null && appointment.getDoctor() != null && doctor != null && appointment.getDoctor().getId().equals(doctor.getId())) {
            appointmentRepository.delete(appointment);
        }
        return "redirect:/doctor/patient/detail?id=" + patientId;
    }
}
