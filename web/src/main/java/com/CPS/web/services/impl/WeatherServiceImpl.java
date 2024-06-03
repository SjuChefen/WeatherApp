package com.CPS.web.services.impl;


import com.CPS.web.dto.WeatherDTO;
import com.CPS.web.models.Weather;
import com.CPS.web.observerpattern.Observer;
import com.CPS.web.observerpattern.Subject;
import com.CPS.web.repository.WeatherRepository;
import com.CPS.web.services.WeatherService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService, Subject {

    @Autowired
    private WeatherRepository weatherRepository;
    private static String API_KEY = "b4568b60752a98ab53ebfe21ad5df3cd"; // Update with your actual API key
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=" + API_KEY + "&units=metric";

    private AtomicReference<String> currentCity = new AtomicReference<>("Odense");
    private List<Observer> observers = new ArrayList<>();

    @Override
    public synchronized void fetchAndSaveWeather(String city) {
        currentCity.set(city);
        String url = String.format(URL, city);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        JSONObject jsonObject = new JSONObject(jsonResponse);
        Long time = jsonObject.getLong("dt");
        Double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        Integer humidity = jsonObject.getJSONObject("main").getInt("humidity");
        Double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        Integer windDirection = jsonObject.getJSONObject("wind").getInt("deg");
        String locationName = jsonObject.getString("name");
        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        String icon = weatherArray.getJSONObject(0).getString("icon"); // Extract icon

        if (!isDuplicate(city, time, temperature, humidity, windSpeed, windDirection)) {
            Weather weather = Weather.builder()
                    .city(city)
                    .time(time)
                    .temperature(temperature)
                    .humidity(humidity)
                    .windSpeed(windSpeed)
                    .windDirection(windDirection)
                    .location(locationName)
                    .icon(icon) // Set the icon
                    .build();

            weatherRepository.save(weather);
            System.out.println("Weather data saved for " + city + " at " + time);

            WeatherDTO weatherData = convertToDTO(weather);
            notifyObservers(weatherData);
        } else {
            System.out.println("Duplicate data found for " + city + " at " + time + ", skipping save.");
        }
    }

    private boolean isDuplicate(String city, Long time, Double temperature, Integer humidity, Double windSpeed, Integer windDirection) {
        Optional<Weather> existingWeather = weatherRepository.findByCityAndTime(city, time);
        if (existingWeather.isPresent()) {
            Weather weather = existingWeather.get();
            return weather.getTemperature().equals(temperature) &&
                    weather.getHumidity().equals(humidity) &&
                    weather.getWindSpeed().equals(windSpeed) &&
                    weather.getWindDirection().equals(windDirection);
        }
        return false;
    }

    @Override
    public List<WeatherDTO> getWeather(String city) {
        return weatherRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WeatherDTO convertToDTO(Weather weather) {
        return WeatherDTO.builder()
                .city(weather.getCity())
                .time(weather.getTime())
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .windSpeed(weather.getWindSpeed())
                .windDirection(weather.getWindDirection())
                .location(weather.getLocation())
                .icon(weather.getIcon()) // Set the icon in DTO
                .build();
    }

    public String getCurrentCity() {
        return currentCity.get();
    }

    @Override
    public void notifyObservers(WeatherDTO weatherData) {
        for (Observer observer : observers) {
            observer.update(weatherData);
        }
    }
}
