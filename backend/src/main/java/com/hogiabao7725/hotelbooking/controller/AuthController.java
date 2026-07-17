package com.hogiabao7725.hotelbooking.controller;

import com.hogiabao7725.hotelbooking.dto.common.ApiResponse;
import com.hogiabao7725.hotelbooking.dto.request.auth.LoginRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.LoginResponse;
import com.hogiabao7725.hotelbooking.dto.response.auth.LoginResult;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.service.AuthService;
import com.hogiabao7725.hotelbooking.service.CookieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Verification email sent.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResult result = authService.login(request);

        ResponseCookie refreshCookie = cookieService.setRefreshTokenCookie(result.refreshToken());

        LoginResponse response = new LoginResponse(result.accessToken(), "Bearer");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Account successfully verified."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        authService.resendVerification(email);
        return ResponseEntity.ok(ApiResponse.success("Verification email resent."));
    }
}
