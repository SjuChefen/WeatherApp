package com.CPS.web.event;

import org.springframework.context.ApplicationEvent;

import com.CPS.web.dto.DTO;

public class WeatherUpdateEvent extends ApplicationEvent {
    private final DTO weatherData;

    public WeatherUpdateEvent(Object source, DTO weatherData) {
        super(source);
        this.weatherData = weatherData;
    }

    public DTO getWeatherData() {
        return weatherData;
    }
}
