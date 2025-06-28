package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.ShiftEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.ShiftRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/nurse/shift")
public class NurseShiftController {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/nurse/dashboard";
        }

        NurseEntity nurse = nurseRepository.findByUser(userEntity);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("startTime").descending());

        Page<ShiftEntity> shifts;

        if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            shifts = shiftRepository.findByNurseIdAndStartTimeBetween(nurse.getId(), startOfDay, endOfDay, pageable);
        } else {
            shifts = shiftRepository.findByNurseId(nurse.getId(), pageable);
        }

        model.addAttribute("shifts", shifts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shifts.getTotalPages());
        model.addAttribute("selectedDate", date);

        return "nurse/shift/list";
    }

}
