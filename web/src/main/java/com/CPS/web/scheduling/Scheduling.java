package com.CPS.web.scheduling;


import com.CPS.web.dto.DTO;
import com.CPS.web.services.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scheduling {

    @Autowired
    private ServiceImpl weatherService;

    @Autowired
    private SimpMessagingTemplate template;

    @Value("${weather.update.rate}")
    private String weatherUpdateRate;

    @Scheduled(fixedRateString = "weather.update.rate")
    public void fetchWeatherData() {
        String city = "Odense"; // Default or retrieve dynamically
        weatherService.fetchAndSaveCityWeather(city);
        List<DTO> weatherData = weatherService.getWeather(city);
        template.convertAndSend("/topic/weather/" + city, weatherData);
    }
}
