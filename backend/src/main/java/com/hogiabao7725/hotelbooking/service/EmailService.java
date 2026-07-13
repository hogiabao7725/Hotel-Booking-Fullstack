package com.hogiabao7725.hotelbooking.service;

public interface EmailService {
    void sendVerification(String to, String username, String token);
}
