package com.ISP392.demo.service;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String message);
}
