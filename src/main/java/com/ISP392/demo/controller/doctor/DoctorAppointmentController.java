package com.ISP392.demo.controller.doctor;

import com.ISP392.demo.dto.UserDto;
import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.enums.RoleEnum;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.RoleRepository;
import com.ISP392.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor/appointment")
public class DoctorAppointmentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("")
    public String viewAppointmentsForDoctor(Model model,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "date", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "5") int size) {

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

}
