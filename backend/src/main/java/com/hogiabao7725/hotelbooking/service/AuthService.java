package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    void verifyEmail(String token);

    void resendVerification(String email);
}
