package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.request.auth.LoginRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RefreshTokenRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.AuthResponse;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void verifyEmail(String token);

    void resendVerification(String email);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
