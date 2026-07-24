package com.hogiabao7725.hotelbooking.mapper;

import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.feature.FeatureUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.feature.FeatureResponse;
import com.hogiabao7725.hotelbooking.entity.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeatureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
    Feature toEntity(FeatureCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            FeatureUpdateRequest request,
            @MappingTarget Feature feature
    );

    @Mapping(target = "iconUrl", source = "resolvedIconUrl")
    FeatureResponse toResponse(Feature feature, String resolvedIconUrl);
}
