package com.ISP392.demo.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.PatientEntity;
import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.enums.GenderEnum;
import com.ISP392.demo.repository.PatientRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.EmailSenderService;
import com.ISP392.demo.service.RoleService;
import com.ISP392.demo.service.UserService;
import com.ISP392.demo.utils.DateUtils;

import jakarta.servlet.http.HttpSession;

@Controller

public class OtpController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public OtpController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "otpConfirm";
    }

    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model) {
        String otpRegister = (String) session.getAttribute("otp-register");
        if (otp.equals(otpRegister)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail((String) session.getAttribute("email"));
            userEntity.setPassword(passwordEncoder.encode((String) session.getAttribute("password")));
            userEntity.setStatus(1);

            RoleEntity role = roleService.findById(4L).get();
            userEntity.setRole(role);

            UserEntity save = userService.saveUser(userEntity);

            PatientEntity patientEntity = new PatientEntity();
            patientEntity.setUser(save);
            patientEntity.setStatus(1);

            patientEntity.setPhone((String) session.getAttribute("phone"));
            patientEntity.setFirstName((String) session.getAttribute("firstName"));
            patientEntity.setLastName((String) session.getAttribute("lastName"));
            patientEntity.setAddress((String) session.getAttribute("address"));

            patientEntity.setGender(GenderEnum.valueOf((String) session.getAttribute("gender")));
            patientEntity.setDateOfBirth(DateUtils.toDate((String) session.getAttribute("dob")));

            patientRepository.save(patientEntity);

            return "redirect:/?successReg=true";
        }
        model.addAttribute("mess", "OTP is not correct! Please check your email.");
        return "otpConfirm";
    }

    @RequestMapping(value = "forgot", method = RequestMethod.GET)
    public String forgot() {
        return "forgot";
    }


    @RequestMapping(value = "forgotPass", method = RequestMethod.POST)
    public String forgotPass(@RequestParam String email, Model model, HttpSession session) {
        try {
            if (!userService.findByEmail(email).isPresent()) {
                model.addAttribute("mess", "Email không tồn tại!");
                return "forgot";
            }
            session.setAttribute("otp-pass", otpCode());
            session.setMaxInactiveInterval(360);
            String subject = "Đây là OTP của bạn";
            String mess = "Xin chào @" + " \n" + email + "Đây là OTP của bạn: " + session.getAttribute("otp-pass") + " Hãy điền vào form!" + "\n Cảm ơn!";
            
            // Gửi email bất đồng bộ với error handling
            this.emailSenderService.sendEmailAsync(email, subject, mess)
                .thenRun(() -> {
                    System.out.println("Forgot password OTP email sent successfully to: " + email);
                })
                .exceptionally(throwable -> {
                    System.err.println("Error sending forgot password OTP email: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
                
            session.setAttribute("email", email);
            return "redirect:/otp-check-pass";
        } catch (Exception e) {
            System.err.println("Forgot password error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("mess", "Có lỗi xảy ra khi gửi OTP. Vui lòng thử lại!");
            return "forgot";
        }
    }

    @RequestMapping(value = "otp-check-pass", method = RequestMethod.GET)
    public String indexOtpPass() {
        return "otpConfirmPass";
    }

    @RequestMapping(value = "confirm-otp-pass", method = RequestMethod.POST)
    public String checkOtpPass(HttpSession session,
                               @RequestParam("otp") String otp,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        String otpRegister = (String) session.getAttribute("otp-pass");
        if (otp.equals(otpRegister)) {
            String email = (String) session.getAttribute("email");
            UserEntity userEntity = userRepository.findByEmail(email).get();
            userEntity.setPassword(passwordEncoder.encode("123456a@A"));
            userService.saveUser(userEntity);

            redirectAttributes.addFlashAttribute("mess", "Mật khẩu được đổi về 123456a@A");
            return "redirect:/";
        }
        model.addAttribute("mess", "OTP nhập sai! Hãy nhập lại!");
        return "otpConfirmPass";
    }

    @RequestMapping(value = "resend-otp-pass", method = RequestMethod.POST)
    @ResponseBody
    public String resendOtpPass(HttpSession session) {
        try {
            System.out.println("===== RESEND OTP REQUEST RECEIVED =====");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Thread: " + Thread.currentThread().getName());
            
            String email = (String) session.getAttribute("email");
            if (email == null) {
                System.err.println("Resend OTP Error: Email not found in session");
                return "error";
            }
            
            System.out.println("Email from session: " + email);
            
            // Generate new OTP
            String newOtp = otpCode();
            System.out.println("Generated new OTP: " + newOtp);
            
            session.setAttribute("otp-pass", newOtp);
            session.setMaxInactiveInterval(360);
            
            // Gửi email bất đồng bộ
            String subject = "Đây là OTP của bạn";
            String mess = "Xin chào @" + " \n" + email + "Đây là OTP của bạn: " + newOtp + " Hãy điền vào form!" + "\n Cảm ơn!";
            
            System.out.println("Sending email to: " + email);
            
            // Gửi email và xử lý kết quả bất đồng bộ
            this.emailSenderService.sendEmailAsync(email, subject, mess)
                .thenRun(() -> {
                    System.out.println("Resend OTP email sent successfully to: " + email);
                })
                .exceptionally(throwable -> {
                    System.err.println("Error sending resend OTP email: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
            
            System.out.println("Returning success response");
            return "success";
        } catch (Exception e) {
            System.err.println("Resend OTP Error: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "resend-otp-register", method = RequestMethod.POST)
    @ResponseBody
    public String resendOtpRegister(HttpSession session) {
        try {
            System.out.println("===== RESEND REGISTER OTP REQUEST RECEIVED =====");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Thread: " + Thread.currentThread().getName());
            
            String email = (String) session.getAttribute("email");
            if (email == null) {
                System.err.println("Resend Register OTP Error: Email not found in session");
                return "error";
            }
            
            System.out.println("Email from session: " + email);
            
            // Generate new OTP
            String newOtp = otpCode();
            System.out.println("Generated new register OTP: " + newOtp);
            
            session.setAttribute("otp-register", newOtp);
            session.setMaxInactiveInterval(360);
            
            // Gửi email bất đồng bộ
            String subject = "Đây là OTP của bạn";
            String mess = "Xin chào @" + " \n" + email + "Đây là OTP của bạn: " + newOtp + " Hãy điền vào form!" + "\n Cảm ơn!";
            
            System.out.println("Sending register email to: " + email);
            
            // Gửi email và xử lý kết quả bất đồng bộ
            this.emailSenderService.sendEmailAsync(email, subject, mess)
                .thenRun(() -> {
                    System.out.println("Resend register OTP email sent successfully to: " + email);
                })
                .exceptionally(throwable -> {
                    System.err.println("Error sending resend register OTP email: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
            
            System.out.println("Returning success response");
            return "success";
        } catch (Exception e) {
            System.err.println("Resend Register OTP Error: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }


}