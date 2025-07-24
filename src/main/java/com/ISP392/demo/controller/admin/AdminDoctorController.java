package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/doctor")
public class AdminDoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public String getDoctorList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String experienceRange,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<DoctorEntity> doctorPage;

        if ((search != null && !search.trim().isEmpty()) || 
            (specialization != null && !specialization.trim().isEmpty()) ||
            (experienceRange != null && !experienceRange.trim().isEmpty())) {
            
            Integer minExperience = null;
            Integer maxExperience = null;

            if ("under5".equals(experienceRange)) {
                maxExperience = 4;
            } else if ("5-10".equals(experienceRange)) {
                minExperience = 5;
                maxExperience = 10;
            } else if ("over10".equals(experienceRange)) {
                minExperience = 11;
            }

            doctorPage = doctorRepository.searchAdminByMultipleFilters(
                search, specialization, minExperience, maxExperience, pageable);
        } else {
            doctorPage = doctorRepository.findAll(pageable);
        }

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("specialization", specialization);
        model.addAttribute("experienceRange", experienceRange);

        return "admin/doctor/list";
    }
} 