package com.ISP392.demo.controller.api;

import com.ISP392.demo.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/admin/api/revenue/daily")
    @ResponseBody
    public List<Map<String, Object>> getDailyRevenue(@RequestParam int month, @RequestParam int year) {
        List<Object[]> result = appointmentRepository.getDailyRevenueByMonth(month, year);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] obj : result) {
            Map<String, Object> item = new HashMap<>();
            item.put("day", obj[0]);
            item.put("revenue", obj[1]);
            data.add(item);
        }
        return data;
    }

}
