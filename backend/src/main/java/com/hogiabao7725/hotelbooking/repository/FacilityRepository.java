package com.hogiabao7725.hotelbooking.repository;

import com.hogiabao7725.hotelbooking.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    boolean existsByNameIgnoreCase(String name);
}
