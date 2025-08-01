package com.ISP392.demo.controller.auth;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.EmailSenderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class FirstLoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/first-login")
    public String firstLoginPage(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        
        // CHỈ PATIENT được tạo bởi lễ tân VÀ lần đầu đăng nhập mới được vào trang xác thực
        if (user == null || user.getRole() == null || 
            !"PATIENT".equals(user.getRole().getName().name()) || 
            !Boolean.TRUE.equals(user.getIsFirstLogin()) ||
            !Boolean.TRUE.equals(user.getCreatedByReceptionist())) {
            return "redirect:/";
        }
        
        model.addAttribute("email", user.getEmail());
        model.addAttribute("showOtpForm", session.getAttribute("otpSent") != null);
        return "first-login-verification";
    }

    @PostMapping("/send-otp")
    public String sendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        
        if (user == null || !"PATIENT".equals(user.getRole().getName().name()) || 
            !Boolean.TRUE.equals(user.getCreatedByReceptionist())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ tài khoản bệnh nhân do lễ tân tạo mới cần xác thực!");
            return "redirect:/";
        }

        try {
            // Tạo mã OTP 6 số
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            // Lưu OTP vào session
            session.setAttribute("verificationOtp", otp);
            session.setAttribute("otpSent", true);
            session.setAttribute("otpEmail", user.getEmail());
            
            // Gửi email OTP bất đồng bộ
            String subject = "Xác thực tài khoản - Mã OTP";
            String body = "Chào " + user.getEmail() + ",\n\n" +
                         "Đây là lần đăng nhập đầu tiên vào hệ thống. Vui lòng sử dụng mã OTP sau để xác thực tài khoản:\n\n" +
                         "MÃ OTP: " + otp + "\n\n" +
                         "Mã OTP có hiệu lực trong 10 phút.\n\n" +
                         "Trân trọng,\n" +
                         "Hệ thống quản lý bệnh viện";
            
            // Gửi email async - không chờ kết quả
            emailSenderService.sendEmailAsync(user.getEmail(), subject, body)
                .thenRun(() -> {
                    System.out.println("✅ Đã gửi email OTP thành công đến: " + user.getEmail());
                })
                .exceptionally(throwable -> {
                    // Log lỗi nếu gửi email thất bại
                    System.err.println("❌ Lỗi gửi email OTP đến " + user.getEmail() + ": " + throwable.getMessage());
                    return null;
                });
            
            redirectAttributes.addFlashAttribute("successMessage", "Mã OTP đã được gửi đến email của bạn!");
            return "redirect:/auth/first-login";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi gửi OTP: " + e.getMessage());
            return "redirect:/auth/first-login";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String inputOtp,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        
        if (user == null || !"PATIENT".equals(user.getRole().getName().name()) || 
            !Boolean.TRUE.equals(user.getCreatedByReceptionist())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ tài khoản bệnh nhân do lễ tân tạo mới cần xác thực!");
            return "redirect:/";
        }

        String sessionOtp = (String) session.getAttribute("verificationOtp");
        String sessionEmail = (String) session.getAttribute("otpEmail");
        
        if (sessionOtp == null || sessionEmail == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Vui lòng gửi lại mã OTP!");
            session.removeAttribute("otpSent");
            return "redirect:/auth/first-login";
        }

        if (!sessionEmail.equals(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc không hợp lệ!");
            session.invalidate();
            return "redirect:/login";
        }

        if (!sessionOtp.equals(inputOtp.trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã OTP không chính xác!");
            return "redirect:/auth/first-login";
        }

        try {
            // Xác thực thành công - cập nhật tài khoản
            user.setIsFirstLogin(false);
            userRepository.save(user);

            // Xóa session OTP
            session.removeAttribute("verificationOtp");
            session.removeAttribute("otpSent");
            session.removeAttribute("otpEmail");

            redirectAttributes.addFlashAttribute("successMessage", "Xác thực tài khoản thành công! Chào mừng bạn đến với hệ thống.");
            return "redirect:/";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xác thực: " + e.getMessage());
            return "redirect:/auth/first-login";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa OTP cũ và gửi lại
        session.removeAttribute("verificationOtp");
        session.removeAttribute("otpSent");
        session.removeAttribute("otpEmail");
        
        redirectAttributes.addFlashAttribute("infoMessage", "Đang gửi lại mã OTP...");
        return sendOtp(session, redirectAttributes);
    }
} 