package com.ISP392.demo.controller.doctor;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor/patient")
public class DoctorPatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("")
    public String patientListPage(Model model,
                                  @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
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

        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<PatientEntity> patients = allPatients.subList(start, end);

        model.addAttribute("patients", patients);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "doctor/patient/list";
    }

    @GetMapping("/detail")
    public String patientDetailPage(Model model,
                                    @RequestParam("id") Long patientId) {
        PatientEntity patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return "redirect:/doctor/patient";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        DoctorEntity doctor = doctorRepository.findByUser(user);

        List<AppointmentEntity> history = patient.getAppointments().stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getId().equals(doctor.getId()))
                .sorted((a1, a2) -> a2.getAppointmentDateTime().compareTo(a1.getAppointmentDateTime()))
                .collect(Collectors.toList());

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", history);

        return "doctor/patient/detail";
    }

}
