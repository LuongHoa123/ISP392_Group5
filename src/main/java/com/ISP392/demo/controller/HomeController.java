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
        return "index";
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