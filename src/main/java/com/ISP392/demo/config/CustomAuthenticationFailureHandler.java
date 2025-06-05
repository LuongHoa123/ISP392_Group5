package com.ISP392.demo.config;

import com.ISP392.demo.exception.UserBlockedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.BadCredentialsException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorMessage = "Tên đăng nhập hoặc mật khẩu không đúng";

        if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof UserBlockedException) {
                errorMessage = cause.getMessage();
            }
        } else if (exception instanceof UserBlockedException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Tên đăng nhập hoặc mật khẩu không đúng";
        }

        response.sendRedirect("/login?error=true&message=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
    }


}
