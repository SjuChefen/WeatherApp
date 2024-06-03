package com.CPS.web.repository;


import com.CPS.web.models.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    List<Weather> findByCity(String city);
    Optional<Weather> findByCityAndTime(String city, Long time);
}