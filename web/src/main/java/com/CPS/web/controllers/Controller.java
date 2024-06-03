package com.CPS.web.controllers;

import com.CPS.web.exceptions.CityNotFoundException;
import com.CPS.web.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
public class Controller {
    private final IService service;

    @Autowired
    public Controller(IService service) {
        this.service = service;
    }

    @GetMapping("/api/weather")
    @ResponseBody
    public ResponseEntity<?> getWeatherData(@RequestParam String city) {
        try {
            service.fetchAndSaveCityWeather(city);
            return ResponseEntity.ok(service.getWeather(city));
        } catch (CityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/weather")
    public String getWeatherPage(@RequestParam String city, Model model) {
        model.addAttribute("city", city);
        return "weather";
    }
}
