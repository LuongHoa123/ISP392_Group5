package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.RequestEntity;
import com.ISP392.demo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/request")
public class AdminRequestController {

    @Autowired
    private RequestRepository requestRepository;

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
        return "redirect:/admin/request?delete=true";
    }
}
