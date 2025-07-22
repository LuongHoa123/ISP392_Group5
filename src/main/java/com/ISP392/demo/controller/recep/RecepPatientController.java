package com.ISP392.demo.controller.recep;

import com.ISP392.demo.dto.AppointmentDto;
import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.repository.RoleRepository;
import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.enums.RoleEnum;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recep/patient")
public class RecepPatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("")
    public String patientListPage(Model model,
                                  @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                  @RequestParam(value = "searchDate", required = false) String searchDate,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) return "redirect:/index";


        List<PatientEntity> allPatients = patientRepository.findAll();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.toLowerCase().trim();
            allPatients = allPatients.stream()
                    .filter(p -> {
                        boolean matchFirstName = p.getFirstName() != null &&
                                p.getFirstName().toLowerCase().contains(keyword);
                        boolean matchLastName = p.getLastName() != null &&
                                p.getLastName().toLowerCase().contains(keyword);
                        boolean matchPhone = p.getPhone() != null &&
                                p.getPhone().contains(searchKeyword.trim());

                        return matchFirstName || matchLastName || matchPhone;
                    })
                    .collect(Collectors.toList());
        }
        if (searchDate != null && !searchDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsedSearchDate;

            try {
                parsedSearchDate = LocalDate.parse(searchDate, formatter);
            } catch (DateTimeParseException e) {
                parsedSearchDate = null;
            }

            if (parsedSearchDate != null) {
                LocalDate finalParsedSearchDate = parsedSearchDate;
                allPatients = allPatients.stream()
                        .filter(p -> p.getDateOfBirth() != null && p.getDateOfBirth().equals(finalParsedSearchDate))
                        .collect(Collectors.toList());
            }

        }

        int totalItems = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<PatientEntity> patients = allPatients.subList(start, end);

        model.addAttribute("patients", patients);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchDate", searchDate);

        return "recep/patient/list";
    }

    @GetMapping("/detail")
    public String patientDetailPage(Model model,
                                    @RequestParam("id") Long patientId) {
        PatientEntity patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return "redirect:/recep/patient";
        }
        List<AppointmentDto> history = patient.getAppointments().stream()
                .sorted((a1, a2) -> a2.getAppointmentDateTime().compareTo(a1.getAppointmentDateTime()))
                .map(a -> {
                    AppointmentDto dto = new AppointmentDto();
                    dto.setId(a.getId());
                    dto.setAppointmentDateTime(a.getAppointmentDateTime());
                    dto.setReason(a.getReason());
                    dto.setName(a.getName());
                    dto.setPhoneNumber(a.getPhoneNumber());
                    dto.setAge(a.getAge());
                    dto.setEmail(a.getEmail());
                    dto.setStatus(a.getStatus());
                    dto.setRoomName(a.getRoom() != null ? a.getRoom().getRoomName() : null);
                    ConclusionEntity ce = a.getConclusionEntity();
                    if (ce != null) {
                        dto.setPrescription(ce.getPrescription());
                        dto.setConclusionContent(ce.getContent());
                    } else {
                        dto.setPrescription(a.getPrescription());
                        dto.setConclusionContent(a.getConclusion());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", history);

        return "recep/patient/detail";
    }

    @GetMapping("/add")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new PatientEntity());
        model.addAttribute("user", new UserEntity());
        return "recep/patient/add";
    }

    @PostMapping("/save")
    public String savePatient(@RequestParam("email") String email,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("phone") String phone,
                             @RequestParam("dateOfBirth") String dateOfBirth,
                             @RequestParam("gender") String gender,
                             @RequestParam("address") String address,
                             RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra email đã tồn tại chưa
            Optional<UserEntity> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                return "redirect:/recep/patient/add";
            }

            // Tạo tài khoản user với mật khẩu mặc định
            String defaultPassword = "123456a@A";
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setStatus(1); // Active
            user.setIsFirstLogin(true); // CHỈ tài khoản do lễ tân tạo mới cần xác thực OTP
            user.setCreatedByReceptionist(true); // Đánh dấu được tạo bởi lễ tân

            // Gán role PATIENT
            RoleEntity patientRole = roleRepository.findByName(RoleEnum.PATIENT);
            if (patientRole == null) {
                throw new RuntimeException("Patient role not found");
            }
            user.setRole(patientRole);

            UserEntity savedUser = userRepository.save(user);

            // Tạo thông tin bệnh nhân
            PatientEntity patient = new PatientEntity();
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setPhone(phone);
            patient.setDateOfBirth(LocalDate.parse(dateOfBirth));
            patient.setGender(GenderEnum.valueOf(gender));
            patient.setAddress(address);
            patient.setUser(savedUser);
            patient.setStatus(1); // Active

            patientRepository.save(patient);

            // Tạo thông báo chi tiết về tài khoản đã tạo
            String successMsg = "Tạo tài khoản bệnh nhân thành công!";
            
            System.out.println("=== DEBUG: Setting flash attributes ===");
            System.out.println("Success Message: " + successMsg);
            System.out.println("Email: " + email);
            System.out.println("Password: " + defaultPassword);
            
            redirectAttributes.addFlashAttribute("successMessage", successMsg);
            redirectAttributes.addFlashAttribute("newAccountEmail", email);
            redirectAttributes.addFlashAttribute("newAccountPassword", defaultPassword);
            
            System.out.println("=== Redirecting to: /recep/patient ===");
            return "redirect:/recep/patient";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo tài khoản: " + e.getMessage());
            return "redirect:/recep/patient/add";
        }
    }
}
