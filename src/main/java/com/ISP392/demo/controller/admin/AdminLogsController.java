package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogsController {

    @Autowired
    private LogsRepository logsRepository;

    @GetMapping("")
    public String patientListPage(Model model,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {

        List<LogsEntity> allLogs = logsRepository.findAll();

        int totalItems = allLogs.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<LogsEntity> logs = allLogs.subList(start, end);

        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/logs/list";
    }
}
