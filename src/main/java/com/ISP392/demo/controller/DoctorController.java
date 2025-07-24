package com.ISP392.demo.controller;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String experienceRange,
            @RequestParam(required = false, defaultValue = "name_asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (specialization != null && specialization.trim().isEmpty()) specialization = null;
        if (gender != null && gender.trim().isEmpty()) gender = null;
        if (experienceRange != null && experienceRange.trim().isEmpty()) experienceRange = null;

        Integer minExperience = null;
        Integer maxExperience = null;

        if ("1-5".equals(experienceRange)) {
            minExperience = 1;
            maxExperience = 5;
        } else if ("5-10".equals(experienceRange)) {
            minExperience = 5;
            maxExperience = 10;
        } else if ("10+".equals(experienceRange)) {
            minExperience = 10;
        }

        Sort sortObj;
        switch (sort) {
            case "name_desc":
                sortObj = Sort.by("firstName").descending();
                break;
            case "exp_asc":
                sortObj = Sort.by("yoe").ascending();
                break;
            case "exp_desc":
                sortObj = Sort.by("yoe").descending();
                break;
            default:
                sortObj = Sort.by("firstName").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<DoctorEntity> doctorPage = doctorRepository.searchByMultipleFilters(
                keyword, specialization, gender, minExperience, maxExperience, pageable
        );

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialization", specialization);
        model.addAttribute("gender", gender);
        model.addAttribute("experienceRange", experienceRange);
        model.addAttribute("sort", sort);

        String queryParams = "?keyword=" + (keyword != null ? keyword : "") +
                "&specialization=" + (specialization != null ? specialization : "") +
                "&experienceRange=" + (experienceRange != null ? experienceRange : "") +
                "&gender=" + (gender != null ? gender : "") +
                "&sort=" + sort;

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

        List<ReviewEntity> reviews = reviewRepository.findByDoctor(doctor);
        Double averageRating = reviewRepository.getAverageRatingByDoctor(doctor);
        Long totalReviews = reviewRepository.getTotalReviewsByDoctor(doctor);

        long excellentCount = reviews.stream().filter(r -> r.getStar() != null && r.getStar() >= 4).count();
        long goodCount = reviews.stream().filter(r -> r.getStar() != null && r.getStar() == 3).count();
        long poorCount = reviews.stream().filter(r -> r.getStar() != null && r.getStar() <= 2).count();

        int excellentPercent = totalReviews > 0 ? (int) Math.round((excellentCount * 100.0) / totalReviews) : 0;
        int goodPercent = totalReviews > 0 ? (int) Math.round((goodCount * 100.0) / totalReviews) : 0;
        int poorPercent = totalReviews > 0 ? (int) Math.round((poorCount * 100.0) / totalReviews) : 0;

        List<ReviewEntity> recentReviews = reviews.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(3)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("doctor", doctor);
        model.addAttribute("reviews", reviews);
        model.addAttribute("recentReviews", recentReviews);
        model.addAttribute("averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("excellentPercent", excellentPercent);
        model.addAttribute("goodPercent", goodPercent);
        model.addAttribute("poorPercent", poorPercent);

        return "doctor-details";
    }
}
