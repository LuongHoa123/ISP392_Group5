package com.ISP392.demo.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {

    @GetMapping("/")
    private String indexHome(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_DOCTOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/doctor/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_RECEPTIONIST".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/recep/dashboard";
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
}