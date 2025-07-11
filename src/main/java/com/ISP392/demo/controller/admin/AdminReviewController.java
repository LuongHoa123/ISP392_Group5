package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

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
                            @RequestParam(value = "search", required = false) String keyword,
                            @RequestParam(value = "filterType", required = false) String filterType,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size) {

        List<ReviewEntity> allReviews = reviewRepository.findAll();
        
        // Tính tổng số đánh giá theo loại
        long totalNegativeReviews = allReviews.stream()
                .filter(review -> review.getStar() != null && review.getStar() <= 2)
                .count();
        
        long totalPositiveReviews = allReviews.stream()
                .filter(review -> review.getStar() != null && review.getStar() >= 3)
                .count();
        
        List<ReviewEntity> list = allReviews;

        // Lọc theo loại đánh giá
        if (filterType != null && !filterType.trim().isEmpty()) {
            if ("negative".equals(filterType)) {
                list = list.stream()
                        .filter(review -> review.getStar() != null && review.getStar() <= 2)
                        .collect(Collectors.toList());
            } else if ("positive".equals(filterType)) {
                list = list.stream()
                        .filter(review -> review.getStar() != null && review.getStar() >= 3)
                        .collect(Collectors.toList());
            }
        }

        // Tìm kiếm theo từ khóa
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            list = list.stream()
                    .filter(reviewEntity -> {
                        boolean match = false;

                        if (reviewEntity.getPatient() != null) {
                            String firstName = reviewEntity.getPatient().getFirstName();
                            String lastName = reviewEntity.getPatient().getLastName();

                            if (firstName != null && firstName.toLowerCase().contains(lowerKeyword)) {
                                match = true;
                            }
                            if (lastName != null && lastName.toLowerCase().contains(lowerKeyword)) {
                                match = true;
                            }
                        }

                        try {
                            int starValue = Integer.parseInt(lowerKeyword);
                            if (reviewEntity.getStar() != null && reviewEntity.getStar() == starValue) {
                                match = true;
                            }
                        } catch (NumberFormatException ignored) {
                        }

                        return match;
                    })
                    .collect(Collectors.toList());
        }

        int totalItems = list.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<ReviewEntity> reviews = list.subList(start, end);

        model.addAttribute("reviews", reviews);
        model.addAttribute("search", keyword);
        model.addAttribute("filterType", filterType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalNegativeReviews", totalNegativeReviews);
        model.addAttribute("totalPositiveReviews", totalPositiveReviews);

        return "admin/review/list";
    }

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<ReviewEntity> reviewEntityOptional = reviewRepository.findById(id);
            if (reviewEntityOptional.isPresent()) {
                ReviewEntity reviewEntity = reviewEntityOptional.get();
                AppointmentEntity appointmentEntity = reviewEntity.getAppointment();
                appointmentEntity.setReviewEntity(null);
                appointmentRepository.save(appointmentEntity);

                reviewRepository.deleteById(id);
                saveLog("Xoá đánh giá có id " + id);

                redirectAttributes.addFlashAttribute("successMessage", "Xóa đánh giá thành công!");
                return "redirect:/admin/review?delete=true";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa đánh giá!");
        }
        return "redirect:/admin/review";
    }
}
