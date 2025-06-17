package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/appointment")
public class AdminAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("")
    public String appointmentSchedulePage(Model model,
                                          @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {

        List<AppointmentEntity> allAppointments = appointmentRepository.findAll();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            allAppointments = allAppointments.stream()
                    .filter(app ->
                            (app.getName() != null && app.getName().toLowerCase().contains(keyword)) ||
                                    (app.getPhoneNumber() != null && app.getPhoneNumber().contains(keyword)) ||
                                    (app.getPatient() != null && (
                                            (app.getPatient().getFirstName() != null && app.getPatient().getFirstName().toLowerCase().contains(keyword)) ||
                                                    (app.getPatient().getLastName() != null && app.getPatient().getLastName().toLowerCase().contains(keyword))
                                    ))
                    )
                    .collect(Collectors.toList());
        }

        int totalItems = allAppointments.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<AppointmentEntity> appointments = allAppointments.subList(start, end);

        model.addAttribute("appointments", appointments);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "admin/appointment/list";
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("appointmentId") Long appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentRepository.deleteById(appointmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá lịch hẹn thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xoá lịch hẹn.");
        }

        return "redirect:/admin/appointment";
    }
}
