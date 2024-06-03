package com.CPS.web.scheduling;


import com.CPS.web.dto.WeatherDTO;
import com.CPS.web.services.impl.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherScheduler {

    @Autowired
    private WeatherServiceImpl weatherService;

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void fetchWeatherData() {
        String city = weatherService.getCurrentCity(); // Get the current city
        weatherService.fetchAndSaveWeather(city);
        List<WeatherDTO> weatherData = weatherService.getWeather(city);
        template.convertAndSend("/topic/weather/" + city, weatherData);
    }
}
