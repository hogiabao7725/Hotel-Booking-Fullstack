package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.constant.StorageConstants;
import com.hogiabao7725.hotelbooking.dto.common.PageResponse;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.feature.FeatureResponse;
import com.hogiabao7725.hotelbooking.entity.Feature;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.mapper.FeatureMapper;
import com.hogiabao7725.hotelbooking.repository.FeatureRepository;
import com.hogiabao7725.hotelbooking.service.FeatureService;
import com.hogiabao7725.hotelbooking.service.FileStorageService;
import com.hogiabao7725.hotelbooking.service.transaction.TransactionalFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;
    private final FileStorageService fileStorageService;
    private final FeatureMapper featureMapper;
    private final TransactionalFileManager transactionalFileManager;

    @Override
    @Transactional
    public FeatureResponse create(FeatureCreateRequest request) {
        validateDuplicateName(request.name());

        Feature feature = featureMapper.toEntity(request);

        String iconPath = fileStorageService.store(
                request.icon(),
                StorageConstants.FEATURES
        );
        feature.setIconUrl(iconPath);

        transactionalFileManager.deleteAfterRollback(iconPath);

        Feature savedFeature = featureRepository.save(feature);

        String resolvedIconUrl =
                fileStorageService.resolveUrl(savedFeature.getIconUrl());

        return featureMapper.toResponse(savedFeature, resolvedIconUrl);
    }

    @Override
    @Transactional
    public FeatureResponse update(Long id, FeatureUpdateRequest request) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Feature", "id", id));

        validateDuplicateNameForUpdate(request.name(), id);

        String oldIconUrl = feature.getIconUrl();

        featureMapper.updateEntity(request, feature);

        boolean hasNewIcon = request.icon() != null && !request.icon().isEmpty();
        if (hasNewIcon) {
            String newIconPath = fileStorageService.store(
                    request.icon(),
                    StorageConstants.FEATURES
            );
            feature.setIconUrl(newIconPath);

            transactionalFileManager.deleteAfterCommit(oldIconUrl);
            transactionalFileManager.deleteAfterRollback(newIconPath);
        }

        Feature updatedFeature = featureRepository.save(feature);

        String resolvedIconUrl = fileStorageService.resolveUrl(updatedFeature.getIconUrl());
        return featureMapper.toResponse(updatedFeature, resolvedIconUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public FeatureResponse getById(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Feature", "id", id));

        String resolvedIconUrl = fileStorageService.resolveUrl(feature.getIconUrl());
        return featureMapper.toResponse(feature, resolvedIconUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FeatureResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feature> featurePage = featureRepository.findAll(pageable);

        Page<FeatureResponse> featureResponsePage = featurePage
                .map(feature -> {
                    String path = fileStorageService.resolveUrl(feature.getIconUrl());
                    return featureMapper.toResponse(feature, path);
                });
        return PageResponse.from(featureResponsePage);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Feature", "id", id));
        String path = feature.getIconUrl();
        featureRepository.delete(feature);
        transactionalFileManager.deleteAfterCommit(path);
    }

    private void validateDuplicateName(String name) {
        if (featureRepository.existsByNameIgnoreCase(name)) {
            throw new AppException(ErrorCode.FEATURE_NAME_ALREADY_EXISTS);
        }
    }

    private void validateDuplicateNameForUpdate(String name, Long id) {
        if (featureRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new AppException(ErrorCode.FEATURE_NAME_ALREADY_EXISTS);
        }
    }
}
