package com.hogiabao7725.hotelbooking.controller;

import com.hogiabao7725.hotelbooking.constant.AppConstants;
import com.hogiabao7725.hotelbooking.dto.common.ApiResponse;
import com.hogiabao7725.hotelbooking.dto.common.PageResponse;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FacilityResponse> create(
            @ModelAttribute @Valid FacilityCreateRequest request
    ) {
        FacilityResponse response = facilityService.create(request);
        return ApiResponse.success("Create facility successfully", response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FacilityResponse> update(
            @PathVariable Long id,
            @ModelAttribute @Valid FacilityUpdateRequest request
    ) {
        FacilityResponse response = facilityService.update(id, request);
        return ApiResponse.success("Update facility successfully", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<FacilityResponse> getById(@PathVariable Long id) {
        FacilityResponse response = facilityService.getById(id);
        return ApiResponse.success("Get facility successfully", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<FacilityResponse>> getAll(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size
    ) {
        PageResponse<FacilityResponse> response = facilityService.getAll(page, size);
        return ApiResponse.success("Get all facilities successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        facilityService.deleteById(id);
        return ApiResponse.success("Delete facility successfully");
    }
}
