package com.ISP392.demo.controller.recep;

import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.RecepEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.RecepRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recep")
public class RecepProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecepRepository recepRepository;

    @GetMapping("/profile")
    public String viewRecepProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/recep/dashboard";
        }

        RecepEntity recep = recepRepository.findByUser(userEntity);
        if (recep != null) {
            model.addAttribute("recep", recep);
            return "recep/profile";
        }

        return "redirect:/";
    }


    @PostMapping("/profile/save")
    public String updateRecepProfile(RecepEntity formRecep) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";

        RecepEntity recep = recepRepository.findByUser(userEntity);
        if (recep == null) return "redirect:/index";

        recep.setFirstName(formRecep.getFirstName());
        recep.setLastName(formRecep.getLastName());
        recep.setPhoneNumber(formRecep.getPhoneNumber());

        recepRepository.save(recep);

        return "redirect:/recep/profile?success=true";
    }

}
