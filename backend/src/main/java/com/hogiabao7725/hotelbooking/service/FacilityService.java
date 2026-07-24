package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;

public interface FacilityService {

    FacilityResponse create(FacilityCreateRequest request);

    FacilityResponse update(Long id, FacilityUpdateRequest request);
}
