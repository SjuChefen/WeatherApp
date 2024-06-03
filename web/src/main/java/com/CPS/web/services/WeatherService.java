package com.CPS.web.services;



import com.CPS.web.dto.WeatherDTO;

import java.util.List;

public interface WeatherService {
    void fetchAndSaveWeather(String city);
    List<WeatherDTO> getWeather(String city);
}
