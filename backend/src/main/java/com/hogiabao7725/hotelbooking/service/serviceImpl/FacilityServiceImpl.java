package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.constant.CloudinaryConstants;
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

import java.util.UUID;

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

        String publicId = UUID.randomUUID().toString();
        String iconUrl = fileStorageService.upload(
                request.icon(),
                CloudinaryConstants.FACILITIES,
                publicId
        );
        facility.setIconUrl(iconUrl);

        facilityRepository.save(facility);
        return facilityMapper.toResponse(facility);
    }

    private void validateDuplicateName(String name) {
        if (facilityRepository.existsByNameIgnoreCase(name)) {
            throw new AppException(ErrorCode.FACILITY_NAME_ALREADY_EXISTS);
        }
    }
}
