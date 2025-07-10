package com.ISP392.demo.controller.doctor;


import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.entity.AppointmentEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }
        session.setAttribute("fullName", doctor.getFirstName() + " " + doctor.getLastName());
        session.setAttribute("avatar", doctor.getAvatar());
        session.setAttribute("specialization", "Chuyên khoa: " + doctor.getSpecialization());

        // Thống kê số bệnh nhân đã khám và chưa khám trong tháng hiện tại
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

        return "doctor/dashboard";
    }

    @GetMapping("/dashboard/patients")
    public String listPatientsByStatus(@RequestParam String status, Model model) {
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
        model.addAttribute("appointments", appointments);
        return "doctor/dashboard-patient-list";
    }
}