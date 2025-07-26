package com.ISP392.demo.controller.doctor;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/doctor")
public class DoctorHomeController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/doctor/dashboard";
        }

        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        session.setAttribute("fullName", doctor.getFirstName() + " " + doctor.getLastName());
        session.setAttribute("avatar", doctor.getAvatar());
        session.setAttribute("specialization", "Chuyên khoa: " + doctor.getSpecialization());

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        Map<Integer, Long> statusCount = new HashMap<>();
        for (Object[] row : appointmentRepository.countAppointmentStatusForDoctorByMonth(doctor, month, year)) {
            Integer status = (Integer) row[0];
            Long count = (Long) row[1];
            statusCount.put(status, count);
        }
        long daKham = statusCount.getOrDefault(1, 0L);
        long chuaKham = statusCount.getOrDefault(2, 0L) + statusCount.getOrDefault(-1, 0L);
        model.addAttribute("daKham", daKham);
        model.addAttribute("chuaKham", chuaKham);

        // Thống kê số ca trực trong tháng hiện tại
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        java.time.LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23,59,59);
        long soCaTrucThang = doctor.getShifts().stream()
            .filter(s -> !s.getStartTime().isBefore(startOfMonth) && !s.getStartTime().isAfter(endOfMonth))
            .count();
        model.addAttribute("soCaTrucThang", soCaTrucThang);

        // Thống kê số bệnh nhân đã khám theo từng tuần trong tháng (chỉ lấy 4 tuần)
        List<AppointmentEntity> daKhamList = appointmentRepository.findByDoctorAndStatusAndMonthAndYear(doctor, 1, month, year);
        java.util.List<Integer> patientsByWeek = new java.util.ArrayList<>();
        for (int w = 1; w <= 4; w++) {
            int count = 0;
            for (AppointmentEntity a : daKhamList) {
                int day = a.getAppointmentDateTime().getDayOfMonth();
                int weekOfMonth = ((day - 1 + now.withDayOfMonth(1).getDayOfWeek().getValue() - 1) / 7) + 1;
                if (weekOfMonth == w) count++;
            }
            patientsByWeek.add(count);
        }
        model.addAttribute("patientsByWeek", patientsByWeek);

        return "doctor/dashboard";
    }

    @GetMapping("/dashboard/patients")
    public String listPatientsByStatus(@RequestParam String status, Model model,
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
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        List<AppointmentEntity> appointments;
        if ("1".equals(status)) {
            appointments = appointmentRepository.findByDoctorAndStatusAndMonthAndYear(doctor, 1, month, year);
            model.addAttribute("title", "Bệnh nhân đã khám trong tháng");
        } else {
            appointments = appointmentRepository.findByDoctorAndStatusInAndMonthAndYear(doctor, List.of(2, -1), month, year);
            model.addAttribute("title", "Bệnh nhân chưa khám trong tháng");
        }
        int totalItems = appointments.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);
        List<AppointmentEntity> appointmentsPage = appointments.subList(start, end);
        model.addAttribute("appointments", appointmentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "doctor/dashboard-patient-list";
    }

    @GetMapping("/dashboard/shifts")
    public String listShiftsInMonth(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/doctor/dashboard";
        }
        DoctorEntity doctor = doctorRepository.findByUser(userEntity);
        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        java.time.LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23,59,59);
        var caTrucThang = doctor.getShifts().stream()
            .filter(s -> !s.getStartTime().isBefore(startOfMonth) && !s.getStartTime().isAfter(endOfMonth))
            .sorted(java.util.Comparator.comparing(s -> s.getStartTime()))
            .toList();
        model.addAttribute("caTrucThang", caTrucThang);
        return "doctor/dashboard-shift-list";
    }
}