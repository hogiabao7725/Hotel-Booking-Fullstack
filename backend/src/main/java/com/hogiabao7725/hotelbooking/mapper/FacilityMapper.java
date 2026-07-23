package com.hogiabao7725.hotelbooking.mapper;

import com.hogiabao7725.hotelbooking.dto.request.facility.FacilityCreateRequest;
import com.hogiabao7725.hotelbooking.dto.response.facility.FacilityResponse;
import com.hogiabao7725.hotelbooking.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
    Facility toEntity(FacilityCreateRequest request);

    @Mapping(target = "iconUrl", source = "resolvedIconUrl")
    FacilityResponse toResponse(Facility facility, String resolvedIconUrl);
}
