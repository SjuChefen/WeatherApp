package com.CPS.web.observerpattern;


import com.CPS.web.dto.WeatherDTO;

public interface Observer {
    void update(WeatherDTO weatherData);
}
