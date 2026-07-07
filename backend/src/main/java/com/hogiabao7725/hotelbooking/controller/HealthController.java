package com.hogiabao7725.hotelbooking.controller;

import com.hogiabao7725.hotelbooking.dto.request.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Void>> checkHealth() {
        return ResponseEntity.ok(
                ApiResponse.success("Service is running smoothly")
        );
    }

}
