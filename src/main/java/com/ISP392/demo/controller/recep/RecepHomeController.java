package com.ISP392.demo.controller.recep;


import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.RecepEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recep")
public class RecepHomeController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecepRepository recepRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/recep/dashboard";
        }

        RecepEntity recep = recepRepository.findByUser(userEntity);
        if (recep == null) {
            return "redirect:/recep/dashboard";
        }
        session.setAttribute("fullName", recep.getFirstName() + " " + recep.getLastName());

        model.addAttribute("countDoctor", doctorRepository.count());
        model.addAttribute("countPatient", patientRepository.countAllByStatus(1));
        model.addAttribute("countUser", userRepository.countAllByStatus(1));

        model.addAttribute("appointmentsWaiting", appointmentRepository.findTop5ByStatusOrderByAppointmentDateTimeDesc(2));

        model.addAttribute("appointmentsPending", appointmentRepository.findTop10ByStatusOrderByAppointmentDateTimeDesc(-1));
        int currentYear = LocalDate.now().getYear();
        List<Object[]> rawWeekdayStats = appointmentRepository.countAppointmentsByWeekday(currentYear);

        Map<Integer, Long> weekdayCountMap = new HashMap<>();
        for (Object[] obj : rawWeekdayStats) {
            Integer dayOfWeek = ((Number) obj[0]).intValue(); // 1–7
            Long count = ((Number) obj[1]).longValue();
            weekdayCountMap.put(dayOfWeek, count);
        }

        String[] weekdayLabels = {"Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = 2; i <= 7; i++) {
            labels.add(weekdayLabels[i - 1]);
            counts.add(weekdayCountMap.getOrDefault(i, 0L));
        }
        labels.add(weekdayLabels[0]);
        counts.add(weekdayCountMap.getOrDefault(1, 0L));

        model.addAttribute("weekdayLabels", labels);
        model.addAttribute("weekdayCounts", counts);

        model.addAttribute("appointmentsPie", appointmentRepository.countAppointmentStatusForCurrentMonth());


        return "recep/dashboard";
    }

}