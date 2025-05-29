package com.ISP392.demo.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("")
    private String profile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(authority -> "ROLE_DOCTOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/doctor/profile";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_PATIENT".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/patient/profile";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_RECEPTIONIST".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/receptionist/profile";
        } else {
            return "index";
        }
    }
}