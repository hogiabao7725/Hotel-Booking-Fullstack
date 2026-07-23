package com.hogiabao7725.hotelbooking.controller;

import com.hogiabao7725.hotelbooking.dto.common.ApiResponse;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
