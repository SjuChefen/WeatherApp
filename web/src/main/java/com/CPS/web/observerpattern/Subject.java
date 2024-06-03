package com.CPS.web.observerpattern;


import com.CPS.web.dto.WeatherDTO;

public interface Subject {
    void notifyObservers(WeatherDTO weatherData);
}
