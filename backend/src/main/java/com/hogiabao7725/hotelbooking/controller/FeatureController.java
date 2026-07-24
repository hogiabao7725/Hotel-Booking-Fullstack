package com.hogiabao7725.hotelbooking.controller;

import com.hogiabao7725.hotelbooking.dto.common.ApiResponse;
import com.hogiabao7725.hotelbooking.dto.common.PageResponse;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.feature.FeatureResponse;
import com.hogiabao7725.hotelbooking.service.FeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FeatureResponse> create(
            @ModelAttribute @Valid FeatureCreateRequest request
    ) {
        FeatureResponse response = featureService.create(request);
        return ApiResponse.success("Create feature successfully", response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FeatureResponse> update(
            @PathVariable Long id,
            @ModelAttribute @Valid FeatureUpdateRequest request
    ) {
        FeatureResponse response = featureService.update(id, request);
        return ApiResponse.success("Update feature successfully", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<FeatureResponse> getById(@PathVariable Long id) {
        FeatureResponse response = featureService.getById(id);
        return ApiResponse.success("Get feature successfully", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<FeatureResponse>> getAll(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size
    ) {
        PageResponse<FeatureResponse> response = featureService.getAll(page, size);
        return ApiResponse.success("Get all features successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        featureService.deleteById(id);
        return ApiResponse.success("Delete feature successfully");
    }
}
