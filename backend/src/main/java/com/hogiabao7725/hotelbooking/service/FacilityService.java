package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.common.PageResponse;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;

public interface FacilityService {

    FacilityResponse create(FacilityCreateRequest request);

    FacilityResponse update(Long id, FacilityUpdateRequest request);

    FacilityResponse getById(Long id);

    PageResponse<FacilityResponse> getAll(int page, int size);

    void deleteById(Long id);
}
