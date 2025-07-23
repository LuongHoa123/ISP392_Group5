package com.ISP392.demo.controller.nurse;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.AppointmentServiceEntity;
import com.ISP392.demo.entity.DiagnosisEntity;
import com.ISP392.demo.entity.ServiceEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.AppointmentServiceRepository;
import com.ISP392.demo.repository.DiagnosisRepository;
import com.ISP392.demo.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/nurse/service")
public class NurseServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;

    @GetMapping("/{id}")
    public String getAppointmentServices(@PathVariable Long id, Model model) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            model.addAttribute("error", "Không tìm thấy lịch hẹn");
            return "error";
        }

        List<ServiceEntity> services = serviceRepository.findAll();
        List<AppointmentServiceEntity> appointmentServices = appointmentServiceRepository.findByAppointmentId(id);

        model.addAttribute("appointment", appointment);
        model.addAttribute("services", services);
        model.addAttribute("appointmentServices", appointmentServices);
        model.addAttribute("diagnosis", appointment.getDiagnosis());
        return "nurse/patient/service";
    }

    @PostMapping("/{id}/add")
    public String addServiceToAppointment(@PathVariable Long id,
                                          @RequestParam Long serviceId,
                                          @RequestParam(required = false) String content,
                                          RedirectAttributes redirectAttributes) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElse(null);
        ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);

        if (appointment == null || service == null) {
            redirectAttributes.addFlashAttribute("error", "Lịch hẹn hoặc dịch vụ không tồn tại");
            return "redirect:/nurse/service/" + id;
        }

        AppointmentServiceEntity appointmentService = new AppointmentServiceEntity();
        appointmentService.setAppointment(appointment);
        appointmentService.setService(service);
        appointmentService.setContent(content != null ? content : service.getContent());


        appointmentServiceRepository.save(appointmentService);

        BigDecimal newTotal = appointmentServiceRepository.findByAppointmentId(id)
                .stream()
                .map(a -> a.getService().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        appointment.setTotalCost(newTotal);
        appointment.setPayCost(BigDecimal.ZERO);
        appointmentRepository.save(appointment);

        redirectAttributes.addAttribute("save", true);
        return "redirect:/nurse/service/" + id;
    }


    @GetMapping("/{appointmentId}/delete-service/{serviceId}")
    @Transactional
    public String removeServiceFromAppointment(@PathVariable Long appointmentId,
                                               @PathVariable Long serviceId,
                                               RedirectAttributes redirectAttributes) {
        try {
            appointmentServiceRepository.deleteByAppointmentIdAndServiceId(appointmentId, serviceId);

            AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
            if (appointment != null) {
                BigDecimal newTotal = appointmentServiceRepository.findByAppointmentId(appointmentId)
                        .stream()
                        .map(a -> a.getService().getPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                appointment.setTotalCost(newTotal);
                appointment.setPayCost(BigDecimal.ZERO);
                appointmentRepository.save(appointment);
            }

            redirectAttributes.addAttribute("delete", true);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ hoặc lịch hẹn.");
        }

        return "redirect:/nurse/service/" + appointmentId;
    }

}
