package com.ISP392.demo.controller.recep;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.repository.*;
import com.ISP392.demo.service.EmailSenderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recep/appointment")
public class RecepAppointmentController {


    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private LogsRepository logsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecepRepository recepRepository;




    @GetMapping("")
    public String appointmentSchedulePage(Model model,
                                          @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {

        List<AppointmentEntity> allAppointments = appointmentRepository.findAll();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            allAppointments = allAppointments.stream()
                    .filter(app ->
                            (app.getName() != null && app.getName().toLowerCase().contains(keyword)) ||
                                    (app.getPhoneNumber() != null && app.getPhoneNumber().contains(keyword)) ||
                                    (app.getPatient() != null && (
                                            (app.getPatient().getFirstName() != null && app.getPatient().getFirstName().toLowerCase().contains(keyword)) ||
                                                    (app.getPatient().getLastName() != null && app.getPatient().getLastName().toLowerCase().contains(keyword))
                                    ))
                    )
                    .collect(Collectors.toList());
        }

        int totalItems = allAppointments.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<AppointmentEntity> appointments = allAppointments.subList(start, end);

        model.addAttribute("appointments", appointments);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "recep/appointment/list";
    }

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/assign")
    public String assignDoctor(@RequestParam Long appointmentId,
                               @RequestParam Long doctorId,
                               @RequestParam Long roomId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
                               RedirectAttributes redirectAttributes) {

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null || appointment.getStatus() == 1 || appointment.getStatus() == 0) {
            return "redirect:/recep/appointment";
        }

        boolean doctorBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(doctorId, appointmentDateTime);
        boolean roomBusy = appointmentRepository.existsByRoomIdAndAppointmentDateTime(roomId, appointmentDateTime);

        if (doctorBusy || roomBusy) {
            redirectAttributes.addFlashAttribute("assignErrorId", appointmentId);
            redirectAttributes.addFlashAttribute("assignErrorMsg",
                    (doctorBusy ? "Bác sĩ" : "") + (doctorBusy && roomBusy ? " và " : "") + (roomBusy ? "phòng" : "") + " đã có lịch tại thời điểm này!");
            redirectAttributes.addFlashAttribute("assignTimeError", appointmentDateTime);
            return "redirect:/recep/appointment";
        }

        appointment.setDoctor(doctorRepository.findById(doctorId).orElse(null));
        appointment.setRoom(roomRepository.findById(roomId).orElse(null));
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setStatus(-1);

        appointmentRepository.save(appointment);



        if (appointment.getEmail() != null) {
            String confirmLink = "http://localhost:8080/appointment/confirm?id=" + appointment.getId();
            String message = "Xin chào " + appointment.getName() + ",\n\n"
                    + "Bạn vừa được chỉ định lịch khám:\n"
                    + "📅 Thời gian: " + appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n"
                    + "👨‍⚕️ Bác sĩ: " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() + "\n"
                    + "🏥 Phòng: " + appointment.getRoom().getRoomName() + "\n\n"
                    + "👉 Vui lòng xác nhận lịch khám tại liên kết sau: " + confirmLink + "\n\n"
                    + "Trân trọng,\nPhòng khám";

            emailSenderService.sendEmail(appointment.getEmail(), "Xác nhận lịch khám", message);
        }

        return "redirect:/recep/appointment";
    }


    @PostMapping("/add")
    public String addAppointment(@RequestParam String name,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String email,
                                 @RequestParam String reason,
                                 @RequestParam Long doctorId,
                                 @RequestParam Long roomId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
                                 RedirectAttributes redirectAttributes) {

        boolean doctorBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(doctorId, appointmentDateTime);
        boolean roomBusy = appointmentRepository.existsByRoomIdAndAppointmentDateTime(roomId, appointmentDateTime);

        if (doctorBusy || roomBusy) {
            redirectAttributes.addFlashAttribute("addError", true);
            redirectAttributes.addFlashAttribute("addErrorMsg",
                    (doctorBusy ? "Bác sĩ" : "") + (doctorBusy && roomBusy ? " và " : "") + (roomBusy ? "phòng" : "") + " đã có lịch tại thời điểm này!");
            return "redirect:/recep/appointment";
        }

        AppointmentEntity newAppt = new AppointmentEntity();
        newAppt.setName(name);
        newAppt.setPhoneNumber(phoneNumber);
        newAppt.setEmail(email);
        newAppt.setReason(reason);
        newAppt.setDoctor(doctorRepository.findById(doctorId).orElse(null));
        newAppt.setRoom(roomRepository.findById(roomId).orElse(null));
        newAppt.setAppointmentDateTime(appointmentDateTime);
        newAppt.setStatus(2);

        appointmentRepository.save(newAppt);

        redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch thành công.");
        return "redirect:/recep/appointment";
    }




    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("appointmentId") Long appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentRepository.deleteById(appointmentId);

            redirectAttributes.addFlashAttribute("successMessage", "Xoá lịch hẹn thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xoá lịch hẹn.");
        }

        return "redirect:/recep/appointment";
    }

}
