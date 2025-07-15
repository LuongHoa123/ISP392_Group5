package com.ISP392.demo.controller.recep;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.InvoiceEntity;
import com.ISP392.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/recep/invoice")
public class RecepInvoiceController {


    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @GetMapping("/{id}")
    public String appointmentSchedulePage(Model model,
                                          @PathVariable(name = "id") Long id) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        List<InvoiceEntity> invoices = invoiceRepository.findAllByAppointmentEntity_Id(id);
        model.addAttribute("invoices", invoices);
        model.addAttribute("appointment", appointment);

        return "recep/appointment/invoice";
    }



    @GetMapping("/detail/{id}")
    public String invoiceDetailPage(@PathVariable("id") Long id, Model model) {
        InvoiceEntity invoice = invoiceRepository.findById(id).orElse(null);
        model.addAttribute("invoice", invoice);
        return "recep/appointment/invoice-detail";
    }
}
