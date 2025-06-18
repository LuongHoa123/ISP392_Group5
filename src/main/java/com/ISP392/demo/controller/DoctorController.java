package com.ISP392.demo.controller;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public String getAllDoctors(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<DoctorEntity> doctorPage = doctorRepository.findAll(pageable);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("baseUrl", "/doctors");

        return "doctors";
    }

    @GetMapping("/search")
    public String searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());

        Page<DoctorEntity> doctorPage = doctorRepository.searchByMultipleFilters(keyword, specialization, pageable);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialization", specialization);
        model.addAttribute("baseUrl", "/doctors/search?keyword=" + (keyword != null ? keyword : "")
                + "&specialization=" + (specialization != null ? specialization : ""));

        return "doctors";
    }

    @GetMapping("/{id}")
    public String getDoctorDetails(@PathVariable Long id, Model model) {
        DoctorEntity doctor = doctorRepository.findById(id).orElse(null);
        if (doctor == null) {
            model.addAttribute("error", "Không tìm thấy bác sĩ");
            return "redirect:/doctors";
        }
        model.addAttribute("doctor", doctor);
        return "doctor-details";
    }
}
