package com.ISP392.demo.controller.admin;


import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String home(Model model) {
        model.addAttribute("countPatient", patientRepository.countAllByStatus(1));
        model.addAttribute("countUser", userRepository.countAllByStatus(1));
        return "admin/dashboard";
    }


}