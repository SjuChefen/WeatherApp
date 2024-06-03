package com.CPS.web.services.impl;

import com.CPS.web.dto.DTO;
import com.CPS.web.models.Weather;
import com.CPS.web.observerpattern.Observer;
import com.CPS.web.observerpattern.Subject;
import com.CPS.web.repository.WeatherRepository;
import com.CPS.web.services.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service, Subject {

    private final WeatherRepository weatherRepository;
    private final String apiKey;
    private final String apiURL;
    private final AtomicReference<String> currentCity = new AtomicReference<>("Odense");
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);

    @Autowired
    public ServiceImpl(WeatherRepository weatherRepository, @Value("${weather.api.key}") String apiKey, @Value("${weather.api.url}") String apiUrl) {
        this.weatherRepository = weatherRepository;
        this.apiKey = apiKey;
        this.apiURL= apiUrl;
    }

    @Override
    public synchronized void fetchAndSaveWeather(String cityName) {
        try {
            currentCity.set(cityName);
            String url = String.format(apiURL, cityName, apiKey);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JSONObject weatherData = new JSONObject(response);
                Weather weatherRecord = extractWeatherData(weatherData, cityName);

                if (weatherRecord != null && !isDuplicateWeatherData(weatherRecord)) {
                    weatherRepository.save(weatherRecord);
                    LOGGER.info("Weather data saved for {} at {}", cityName, weatherRecord.getTime());
                    notifyObservers(convertToWeatherDTO(weatherRecord));
                } else {
                    LOGGER.info("Duplicate weather data for {} at {}, skipping save.", cityName, weatherRecord.getTime());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching weather data for {}: {}", cityName, e.getMessage());
        }
    }

    private Weather extractWeatherData(JSONObject weatherData, String cityName) {
        try {
            long timestamp = weatherData.getLong("dt");
            double temperature = weatherData.getJSONObject("main").getDouble("temp");
            int humidity = weatherData.getJSONObject("main").getInt("humidity");
            double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
            int windDirection = weatherData.getJSONObject("wind").getInt("deg");
            String locationName = weatherData.getString("name");
            String weatherIcon = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");

            return Weather.builder()
                    .city(cityName)
                    .time(timestamp)
                    .temperature(temperature)
                    .humidity(humidity)
                    .windSpeed(windSpeed)
                    .windDirection(windDirection)
                    .location(locationName)
                    .icon(weatherIcon)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error extracting weather data for {}: {}", cityName, e.getMessage());
            return null;
        }
    }

    private boolean isDuplicateWeatherData(Weather weatherRecord) {
        Optional<Weather> existingWeather = weatherRepository.findByCityAndTime(weatherRecord.getCity(), weatherRecord.getTime());
        return existingWeather.isPresent() && isIdenticalWeatherData(existingWeather.get(), weatherRecord);
    }

    private boolean isIdenticalWeatherData(Weather existingWeather, Weather newWeather) {
        return existingWeather.getTemperature().equals(newWeather.getTemperature())
                && existingWeather.getHumidity().equals(newWeather.getHumidity())
                && existingWeather.getWindSpeed().equals(newWeather.getWindSpeed())
                && existingWeather.getWindDirection().equals(newWeather.getWindDirection());
    }

    @Override
    public List<DTO> getWeather(String cityName) {
        return weatherRepository.findByCity(cityName).stream()
                .map(this::convertToWeatherDTO)
                .collect(Collectors.toList());
    }

    private DTO convertToWeatherDTO(Weather weather) {
        return DTO.builder()
                .city(weather.getCity())
                .time(weather.getTime())
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .windSpeed(weather.getWindSpeed())
                .windDirection(weather.getWindDirection())
                .location(weather.getLocation())
                .icon(weather.getIcon())
                .build();
    }

    public String getCurrentCity() {
        return currentCity.get();
    }

    @Override
    public void notifyObservers(DTO DTO) {
        observers.forEach(observer -> observer.update(DTO));
    }
}