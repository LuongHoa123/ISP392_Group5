//package com.ISP392.demo.controller.doctor;
//
//import com.ISP392.demo.entity.DiagnosisEntity;
//import com.ISP392.demo.entity.AppointmentEntity;
//import com.ISP392.demo.repository.DiagnosisRepository;
//import com.ISP392.demo.repository.AppointmentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//
//@Controller
//@RequestMapping("/doctor/appointment/diagnosis")
//public class DoctorDiagnosisController {
//
//    @Autowired
//    private DiagnosisRepository diagnosisRepository;
//
//    @Autowired
//    private AppointmentRepository appointmentRepository;
//
//    @GetMapping("/{appointmentId}")
//    public String showDiagnosisForm(@PathVariable("appointmentId") Long appointmentId, Model model) {
//        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
//
//        if (appointment == null) {
//            model.addAttribute("error", "Lịch hẹn không tồn tại");
//            return "error";
//        }
//
//        List<DiagnosisEntity> diagnoses = diagnosisRepository.findByAppointment(appointment);
//        model.addAttribute("appointment", appointment);
//        model.addAttribute("diagnoses", diagnoses);
//        model.addAttribute("newDiagnosis", new DiagnosisEntity());
//
//        return "doctor/appointment/diagnosis";
//    }
//
//    @PostMapping("/update")
//    public String updateDiagnosis(@RequestParam("diagnosisId") Long diagnosisId,
//                                  @RequestParam("content") String content,
//                                  @RequestParam("level") Integer level,
//                                  @RequestParam("price") BigDecimal price) {
//
//        DiagnosisEntity diagnosis = diagnosisRepository.findById(diagnosisId).orElse(null);
//
//        if (diagnosis != null) {
//            diagnosis.setContent(content);
//            diagnosisRepository.save(diagnosis);
//
//            AppointmentEntity appointment = diagnosis.getAppointment();
//            updateTotalCost(appointment);
//            return "redirect:/doctor/appointment/diagnosis/" + appointment.getId() + "?update=true";
//        }
//
//        return "redirect:/doctor/appointment/diagnosis/?update=false";
//    }
//
//
//    @PostMapping("/{appointmentId}/add")
//    public String addDiagnosis(@PathVariable("appointmentId") Long appointmentId,
//                               @ModelAttribute("newDiagnosis") DiagnosisEntity diagnosis,
//                               Model model) {
//        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
//
//        if (appointment == null) {
//            model.addAttribute("error", "Lịch hẹn không tồn tại");
//            return "error";
//        }
//
//        diagnosis.setAppointment(appointment);
//        diagnosisRepository.save(diagnosis);
//
//        appointment = appointmentRepository.findById(appointmentId).orElseThrow();
//        updateTotalCost(appointment);
//
//        return "redirect:/doctor/appointment/diagnosis/" + appointmentId + "?save=true";
//    }
//
//
//    @GetMapping("/{appointmentId}/delete/{diagnosisId}")
//    public String deleteDiagnosis(@PathVariable("appointmentId") Long appointmentId,
//                                  @PathVariable("diagnosisId") Long diagnosisId) {
//        DiagnosisEntity diagnosis = diagnosisRepository.findById(diagnosisId).orElse(null);
//
//        if (diagnosis != null) {
//            AppointmentEntity appointment = diagnosis.getAppointment();
//            diagnosisRepository.delete(diagnosis);
//
//            appointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
//            updateTotalCost(appointment);
//        }
//
//        return "redirect:/doctor/appointment/diagnosis/" + appointmentId + "?delete=true";
//    }
//
//
//    private void updateTotalCost(AppointmentEntity appointment) {
//        BigDecimal total = appointment.getDiagnosisEntities()
//                .stream()
//                .map(DiagnosisEntity::getPrice)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        appointment.setTotalCost(total);
//        appointmentRepository.save(appointment);
//    }
//
//}