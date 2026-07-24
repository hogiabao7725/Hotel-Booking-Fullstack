package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.constant.StorageConstants;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.entity.Facility;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.mapper.FacilityMapper;
import com.hogiabao7725.hotelbooking.repository.FacilityRepository;
import com.hogiabao7725.hotelbooking.service.FacilityService;
import com.hogiabao7725.hotelbooking.service.FileStorageService;
import com.hogiabao7725.hotelbooking.service.transaction.TransactionalFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final FileStorageService fileStorageService;
    private final FacilityMapper facilityMapper;
    private final TransactionalFileManager transactionalFileManager;

    @Override
    @Transactional
    public FacilityResponse create(FacilityCreateRequest request) {
        validateDuplicateName(request.name());

        Facility facility = facilityMapper.toEntity(request);

        String iconPath = fileStorageService.store(
                request.icon(),
                StorageConstants.FACILITIES
        );
        facility.setIconUrl(iconPath);

        transactionalFileManager.deleteAfterRollback(iconPath);

        Facility savedFacility = facilityRepository.save(facility);

        String resolvedIconUrl =
                fileStorageService.resolveUrl(savedFacility.getIconUrl());

        return facilityMapper.toResponse(savedFacility, resolvedIconUrl);
    }

    @Override
    @Transactional
    public FacilityResponse update(Long id, FacilityUpdateRequest request) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Facility", "id", id));

        validateDuplicateNameForUpdate(request.name(), id);

        String oldIconUrl = facility.getIconUrl();

        facilityMapper.updateEntity(request, facility);

        boolean hasNewIcon = request.icon() != null && !request.icon().isEmpty();
        if (hasNewIcon) {
            String newIconPath = fileStorageService.store(
                    request.icon(),
                    StorageConstants.FACILITIES
            );
            facility.setIconUrl(newIconPath);

            // Clean up old file on commit, and new file on rollback
            transactionalFileManager.deleteAfterCommit(oldIconUrl);
            transactionalFileManager.deleteAfterRollback(newIconPath);
        }

        Facility updatedFacility = facilityRepository.save(facility);

        String resolvedIconUrl = fileStorageService.resolveUrl(updatedFacility.getIconUrl());
        return facilityMapper.toResponse(updatedFacility, resolvedIconUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityResponse getById(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Facility", "id", id));

        String resolvedIconUrl = fileStorageService.resolveUrl(facility.getIconUrl());
        return facilityMapper.toResponse(facility, resolvedIconUrl);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Facility", "id", id));
        String path = facility.getIconUrl();
        facilityRepository.delete(facility);
        transactionalFileManager.deleteAfterCommit(path);
    }

    private void validateDuplicateName(String name) {
        if (facilityRepository.existsByNameIgnoreCase(name)) {
            throw new AppException(ErrorCode.FACILITY_NAME_ALREADY_EXISTS);
        }
    }

    private void validateDuplicateNameForUpdate(String name, Long id) {
        if (facilityRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new AppException(ErrorCode.FACILITY_NAME_ALREADY_EXISTS);
        }
    }
}
