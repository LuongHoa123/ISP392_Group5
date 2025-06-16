package com.ISP392.demo.service;

import java.util.concurrent.CompletableFuture;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String message);
    CompletableFuture<Void> sendEmailAsync(String to, String subject, String message);
}
