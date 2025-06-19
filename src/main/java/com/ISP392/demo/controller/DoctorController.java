package com.ISP392.demo.controller;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
            @RequestParam(required = false) String experienceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minCreatedAt = null;
        LocalDateTime maxCreatedAt = null;

        if ("1-5".equals(experienceRange)) {
            minCreatedAt = now.minusYears(1).plusDays(1);
            maxCreatedAt = now.minusYears(5);
        } else if ("5-10".equals(experienceRange)) {
            minCreatedAt = now.minusYears(5).plusDays(1);
            maxCreatedAt = now.minusYears(10);
        } else if ("10+".equals(experienceRange)) {
            minCreatedAt = now.minusYears(10);
        }

        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.trim().replaceAll("\\s+", " ");
        }

        Page<DoctorEntity> doctorPage = doctorRepository.searchByMultipleFilters(
                keyword, specialization, minCreatedAt, maxCreatedAt, pageable);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialization", specialization);
        model.addAttribute("experienceRange", experienceRange);
        String queryParams = "?keyword=" + (keyword != null ? keyword : "") +
                "&specialization=" + (specialization != null ? specialization : "") +
                "&experienceRange=" + (experienceRange != null ? experienceRange : "");

        model.addAttribute("baseUrl", "/doctors/search" + queryParams);

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
