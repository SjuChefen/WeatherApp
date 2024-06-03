package com.CPS.web.event;

import com.CPS.web.dto.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WeatherUpdateListener {

    private final SimpMessagingTemplate template;

    @Autowired
    public WeatherUpdateListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void handleWeatherUpdate(WeatherUpdateEvent event) {
        DTO weatherData = event.getWeatherData();
        String city = weatherData.getCity();
        template.convertAndSend("/topic/weather/" + city, weatherData);
    }
}
