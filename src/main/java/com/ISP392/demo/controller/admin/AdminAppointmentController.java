package com.ISP392.demo.controller.admin;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.LogsRepository;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.UserRepository;

@Controller
@RequestMapping("/admin/appointment")
public class AdminAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoomRepository roomRepository;

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
    public String appointmentSchedulePage(Model model,
                                          @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                          @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                          @RequestParam(value = "sortBy", defaultValue = "appointmentDateTime") String sortBy,
                                          @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {

        List<AppointmentEntity> allAppointments = appointmentRepository.findAll();

        // Filter by search keyword
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            allAppointments = allAppointments.stream()
                    .filter(app -> {
                        if (keyword == null || keyword.trim().isEmpty()) {
                            return true;
                        }
                        
                        String lowerKeyword = keyword.toLowerCase().trim();
                        
                        // Tìm trong tên appointment
                        if (app.getName() != null && app.getName().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                        
                        // Tìm trong số điện thoại
                        if (app.getPhoneNumber() != null && app.getPhoneNumber().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                        
                        // Tìm trong email
                        if (app.getEmail() != null && app.getEmail().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                        
                        // Tìm trong thông tin patient
                        if (app.getPatient() != null) {
                            if ((app.getPatient().getFirstName() != null && 
                                 app.getPatient().getFirstName().toLowerCase().contains(lowerKeyword)) ||
                                (app.getPatient().getLastName() != null && 
                                 app.getPatient().getLastName().toLowerCase().contains(lowerKeyword))) {
                                return true;
                            }
                        }
                        
                        // Tìm trong thông tin doctor
                        if (app.getDoctor() != null) {
                            if ((app.getDoctor().getFirstName() != null && 
                                 app.getDoctor().getFirstName().toLowerCase().contains(lowerKeyword)) ||
                                (app.getDoctor().getLastName() != null && 
                                 app.getDoctor().getLastName().toLowerCase().contains(lowerKeyword))) {
                                return true;
                            }
                        }
                        
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        // Filter by status
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            try {
                Integer status = Integer.parseInt(statusFilter);
                allAppointments = allAppointments.stream()
                        .filter(app -> app.getStatus() != null && app.getStatus().equals(status))
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Invalid status filter, ignore
            }
        }

        // Sort the appointments
        Comparator<AppointmentEntity> comparator = getComparator(sortBy, sortDir);
        if (comparator != null) {
            allAppointments = allAppointments.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        // Pagination
        int totalItems = allAppointments.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<AppointmentEntity> appointments = allAppointments.subList(start, end);

        // Add attributes to model
        model.addAttribute("appointments", appointments);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalItems);
        model.addAttribute("pageSize", size);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "admin/appointment/list";
    }

    /**
     * Get comparator for sorting appointments
     */
    private Comparator<AppointmentEntity> getComparator(String sortBy, String sortDir) {
        Comparator<AppointmentEntity> comparator = null;
        
        switch (sortBy) {
            case "name":
                comparator = Comparator.comparing(app -> 
                    app.getName() != null ? app.getName().toLowerCase() : "", 
                    Comparator.nullsLast(String::compareToIgnoreCase));
                break;
                
            case "appointmentDateTime":
                comparator = Comparator.comparing(AppointmentEntity::getAppointmentDateTime,
                    Comparator.nullsLast(LocalDateTime::compareTo));
                break;
                
            case "status":
                comparator = Comparator.comparing(AppointmentEntity::getStatus,
                    Comparator.nullsLast(Integer::compareTo));
                break;
                
            case "phoneNumber":
                comparator = Comparator.comparing(app -> 
                    app.getPhoneNumber() != null ? app.getPhoneNumber() : "",
                    Comparator.nullsLast(String::compareToIgnoreCase));
                break;
                
            case "email":
                comparator = Comparator.comparing(app -> 
                    app.getEmail() != null ? app.getEmail().toLowerCase() : "",
                    Comparator.nullsLast(String::compareToIgnoreCase));
                break;
                
            case "doctorName":
                comparator = Comparator.comparing(app -> {
                    if (app.getDoctor() != null) {
                        String firstName = app.getDoctor().getFirstName() != null ? app.getDoctor().getFirstName() : "";
                        String lastName = app.getDoctor().getLastName() != null ? app.getDoctor().getLastName() : "";
                        return (firstName + " " + lastName).trim().toLowerCase();
                    }
                    return "";
                }, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
                
            default:
                // Default sort by appointmentDateTime desc
                comparator = Comparator.comparing(AppointmentEntity::getAppointmentDateTime,
                    Comparator.nullsLast(LocalDateTime::compareTo)).reversed();
                break;
        }
        
        // Apply sort direction
        if ("desc".equalsIgnoreCase(sortDir) && comparator != null && !sortBy.equals("appointmentDateTime")) {
            comparator = comparator.reversed();
        } else if ("asc".equalsIgnoreCase(sortDir) && sortBy.equals("appointmentDateTime")) {
            // For appointmentDateTime, default is desc, so reverse for asc
            comparator = comparator.reversed();
        }
        
        return comparator;
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("appointmentId") Long appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentRepository.deleteById(appointmentId);
            saveLog("Xoá lịch hẹn có id: " + appointmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá lịch hẹn thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xoá lịch hẹn.");
        }

        return "redirect:/admin/appointment";
    }
}