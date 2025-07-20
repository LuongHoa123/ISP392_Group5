package com.ISP392.demo.controller;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    private String indexHome(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        List<ReviewEntity> topReviews = reviewRepository.findTop3ByOrderByStarDesc();
        model.addAttribute("reviews", topReviews);

        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_DOCTOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/doctor/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_RECEPTIONIST".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/recep/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_NURSE".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/nurse/dashboard";
        } else {
            return "index";
        }
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return indexHome(model);
    }

    @GetMapping("/index")
    public String index(Model model) {
        return indexHome(model);
    }

    @GetMapping("/appointment/confirm")
    public String confirmAppointment(@RequestParam("id") Long id) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(2);
            appointmentRepository.save(appointment);
        }
        return "confirmation-success";
    }

    @GetMapping("/appointment/cancel")
    public String cancelByPatient(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null || appointment.getStatus() != -1) {
            return "redirect:/";
        }

        appointment.setStatus(0);
        appointment.setNoteCancel("Huỷ bởi bệnh nhân qua email");
        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("message", "Bạn đã huỷ lịch thành công!");
        return "confirmation-success";
    }

}