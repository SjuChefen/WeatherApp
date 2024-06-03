package com.CPS.web.services;



import com.CPS.web.dto.DTO;

import java.util.List;

public interface Service {
    void fetchAndSaveWeather(String city);
    List<DTO> getWeather(String city);
}
