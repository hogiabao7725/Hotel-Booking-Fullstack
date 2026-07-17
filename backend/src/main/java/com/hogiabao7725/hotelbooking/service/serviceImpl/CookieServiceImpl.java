package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.CookieProperties;
import com.hogiabao7725.hotelbooking.config.properties.RefreshTokenProperties;
import com.hogiabao7725.hotelbooking.service.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    private final CookieProperties cookieProperties;
    private final RefreshTokenProperties refreshTokenProperties;

    @Override
    public ResponseCookie setRefreshTokenCookie(String token) {
        CookieProperties.CookieDetail detail = cookieProperties.refresh();
        return ResponseCookie.from(detail.name(), token)
                .httpOnly(true)
                .secure(detail.secure())
                .path(detail.path())
                .maxAge(refreshTokenProperties.expiration().getSeconds())
                .sameSite(detail.sameSite())
                .build();
    }

    @Override
    public ResponseCookie clearRefreshTokenCookie() {
        CookieProperties.CookieDetail detail = cookieProperties.refresh();
        return ResponseCookie.from(detail.name(), "")
                .httpOnly(true)
                .secure(detail.secure())
                .path(detail.path())
                .maxAge(0)
                .sameSite(detail.sameSite())
                .build();
    }
}
