package com.ISP392.demo.controller.recep;


import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String home(Model model) {
        model.addAttribute("countDoctor", doctorRepository.count());
        model.addAttribute("countPatient", patientRepository.countAllByStatus(1));
        model.addAttribute("countUser", userRepository.countAllByStatus(1));

        model.addAttribute("appointmentsWaiting", appointmentRepository.findTop5ByStatusOrderByAppointmentDateTimeDesc(2));

        model.addAttribute("appointmentsPending", appointmentRepository.findTop10ByStatusOrderByAppointmentDateTimeDesc(-1));
        int currentYear = LocalDate.now().getYear();
        List<Object[]> rawMonthlyStats = appointmentRepository.countAppointmentsByMonth(currentYear);

        Map<Integer, Long> statsMap = new HashMap<>();
        for (Object[] obj : rawMonthlyStats) {
            Integer month = (Integer) obj[0];
            Long count = (Long) obj[1];
            statsMap.put(month, count);
        }

        List<List<Object>> fullMonthStats = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            long count = statsMap.getOrDefault(i, 0L);
            fullMonthStats.add(List.of(i, count));
        }
        model.addAttribute("appointmentsByMonth", fullMonthStats);
        model.addAttribute("appointmentsPie", appointmentRepository.countAppointmentStatusForCurrentMonth());

        return "recep/dashboard";
    }

}