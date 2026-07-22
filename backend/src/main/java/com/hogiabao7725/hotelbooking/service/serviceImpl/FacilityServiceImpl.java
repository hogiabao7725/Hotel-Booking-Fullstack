package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.constant.StorageConstants;
import com.hogiabao7725.hotelbooking.dto.request.facility.CreateFacilityRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.entity.Facility;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.mapper.FacilityMapper;
import com.hogiabao7725.hotelbooking.repository.FacilityRepository;
import com.hogiabao7725.hotelbooking.service.FacilityService;
import com.hogiabao7725.hotelbooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    private final FileStorageService fileStorageService;

    private final FacilityMapper facilityMapper;

    @Override
    @Transactional
    public FacilityResponse create(CreateFacilityRequest request) {
        validateDuplicateName(request.name());

        Facility facility = facilityMapper.toEntity(request);
        String iconPath = fileStorageService.store(request.icon(), StorageConstants.FACILITIES);
        facility.setIconUrl(iconPath);
        facilityRepository.save(facility);

        return facilityMapper.toResponse(facility);
    }

    private void validateDuplicateName(String name) {
        if (facilityRepository.existsByNameIgnoreCase(name)) {
            throw new AppException(ErrorCode.FACILITY_NAME_ALREADY_EXISTS);
        }
    }
}
