package com.CPS.web.controller;

import com.CPS.web.service.WeatherInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class WeatherInfoController {

    private final WeatherInfoService weatherService;

    @Autowired
    public WeatherInfoController(WeatherInfoService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/location/{city}")
    public String getWeatherInfo(@PathVariable String city, Model model) {
        String weatherData = weatherService.getWeatherData(city);
        model.addAttribute("city", city);
        model.addAttribute("weatherData", weatherData);
        return "weather";
    }
}
