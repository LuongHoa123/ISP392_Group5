package com.ISP392.demo.config;

import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FirstLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Bỏ qua nếu chưa đăng nhập hoặc là anonymous user
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return true;
        }

        String requestURI = request.getRequestURI();
        
        // Bỏ qua các URL không cần kiểm tra
        if (requestURI.startsWith("/auth/first-login") || 
            requestURI.startsWith("/auth/send-otp") ||
            requestURI.startsWith("/auth/verify-otp") ||
            requestURI.startsWith("/auth/resend-otp") ||
            requestURI.startsWith("/login") ||
            requestURI.startsWith("/logout") ||
            requestURI.startsWith("/assets") ||
            requestURI.startsWith("/css") ||
            requestURI.startsWith("/js") ||
            requestURI.startsWith("/images")) {
            return true;
        }

        // Kiểm tra user có cần xác thực lần đầu không
        String username = auth.getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);
        
        if (user != null && user.getIsFirstLogin() != null && user.getIsFirstLogin()) {
            response.sendRedirect("/auth/first-login");
            return false;
        }

        return true;
    }
} 