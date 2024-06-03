package com.CPS.web.controllers;

import com.CPS.web.dto.WeatherDTO;
import com.CPS.web.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class WeatherController {
    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather")
    @ResponseBody
    public List<WeatherDTO> getWeatherData(@RequestParam String city) {
        // Fetch and save new data from the OpenWeatherMap API
        weatherService.fetchAndSaveWeather(city);

        // Retrieve the latest data for the specified city
        return weatherService.getWeather(city);
    }

    @GetMapping("/weather")
    public String getWeatherPage(@RequestParam String city, Model model) {
        model.addAttribute("city", city);
        return "weather";
    }
}
