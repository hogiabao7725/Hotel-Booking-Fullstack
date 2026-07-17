package com.hogiabao7725.hotelbooking.service;

import org.springframework.http.ResponseCookie;

public interface CookieService {
    ResponseCookie setRefreshTokenCookie(String token);
    ResponseCookie clearRefreshTokenCookie();
}
