package com.CPS.web.controllers;

import com.CPS.web.dto.DTO;
import com.CPS.web.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    private final Service service;

    @Autowired
    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping("/api/weather")
    @ResponseBody
    public List<DTO> getWeatherData(@RequestParam String city) {
        service.fetchAndSaveWeather(city);

        return service.getWeather(city);
    }

    @GetMapping("/weather")
    public String getWeatherPage(@RequestParam String city, Model model) {
        model.addAttribute("city", city);
        return "weather";
    }
}
