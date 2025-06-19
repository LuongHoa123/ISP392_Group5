package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.entity.RequestEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.LogsRepository;
import com.ISP392.demo.repository.RequestRepository;
import com.ISP392.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/request")
public class AdminRequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogsRepository logsRepository;


    private void saveLog(String content) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            LogsEntity log = new LogsEntity();
            log.setContent(content);
            log.setUser(user);
            log.setCreatedAt(LocalDateTime.now());
            logsRepository.save(log);
        }
    }


    @GetMapping("")
    public String listRooms(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size) {

        List<RequestEntity> allRequests = requestRepository.findAll();

        int totalItems = allRequests.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<RequestEntity> requests = allRequests.subList(start, end);

        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/request/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id) {
        requestRepository.deleteById(id);
        saveLog("Xoá yêu cầu mở khoá có id: " + id);
        return "redirect:/admin/request?delete=true";
    }
}
