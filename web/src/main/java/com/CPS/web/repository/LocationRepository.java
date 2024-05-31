package com.CPS.web.repository;

import com.CPS.web.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameAndCountry(String name, String country);
}
