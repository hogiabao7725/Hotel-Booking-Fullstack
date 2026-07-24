package com.hogiabao7725.hotelbooking.mapper;

import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityUpdateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
    Facility toEntity(FacilityCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            FacilityUpdateRequest request,
            @MappingTarget Facility facility
    );

    @Mapping(target = "iconUrl", source = "resolvedIconUrl")
    FacilityResponse toResponse(Facility facility, String resolvedIconUrl);
}
