package com.ISP392.demo.controller.patient;

import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.enums.GenderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recep/patient")
public class RecepPatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("")
    public String patientListPage(Model model,
                                  @RequestParam(value = "firstName", required = false) String firstName,
                                  @RequestParam(value = "lastName", required = false) String lastName,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "gender", required = false) String gender,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {

        List<PatientEntity> allPatients = patientRepository.findAll();

        if (firstName != null && !firstName.isEmpty()) {
            allPatients = allPatients.stream()
                    .filter(p -> p.getFirstName() != null && p.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (lastName != null && !lastName.isEmpty()) {
            allPatients = allPatients.stream()
                    .filter(p -> p.getLastName() != null && p.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (phone != null && !phone.isEmpty()) {
            allPatients = allPatients.stream()
                    .filter(p -> p.getPhone() != null && p.getPhone().contains(phone))
                    .collect(Collectors.toList());
        }

        if (gender != null && !gender.isEmpty()) {
            GenderEnum genderEnum = GenderEnum.valueOf(gender);
            allPatients = allPatients.stream()
                    .filter(p -> p.getGender() == genderEnum)
                    .collect(Collectors.toList());
        }

        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<PatientEntity> patients = allPatients.subList(start, end);

        model.addAttribute("patients", patients);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("phone", phone);
        model.addAttribute("gender", gender);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "recep/patient/list";
    }
}
