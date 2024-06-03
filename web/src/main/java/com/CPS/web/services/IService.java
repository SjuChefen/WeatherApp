package com.CPS.web.services;



import com.CPS.web.dto.DTO;

import java.util.List;

public interface IService {
    void fetchAndSaveCityWeather(String city);
    List<DTO> getWeather(String city);
}
