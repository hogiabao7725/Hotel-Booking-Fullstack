package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.request.facility.CreateFacilityRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;

public interface FacilityService {

    FacilityResponse create(CreateFacilityRequest request);
}
