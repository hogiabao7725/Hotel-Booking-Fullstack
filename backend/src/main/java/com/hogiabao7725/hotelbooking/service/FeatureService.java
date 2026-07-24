package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.dto.common.PageResponse;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.feature.FeatureResponse;

public interface FeatureService {

    FeatureResponse create(FeatureCreateRequest request);

    FeatureResponse update(Long id, FeatureUpdateRequest request);

    FeatureResponse getById(Long id);

    PageResponse<FeatureResponse> getAll(int page, int size);

    void deleteById(Long id);
}
