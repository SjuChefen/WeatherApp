package com.CPS.web.services.impl;

import com.CPS.web.dto.DTO;
import com.CPS.web.event.WeatherUpdateEvent;
import com.CPS.web.models.Weather;
import com.CPS.web.repository.WeatherRepository;
import com.CPS.web.services.IService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ServiceImpl implements IService {

    private final WeatherRepository weatherRepository;
    private final String apiKey;
    private final String apiURL;
    private final AtomicReference<String> currentCity = new AtomicReference<>("Odense");
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ServiceImpl(WeatherRepository weatherRepository, @Value("${weather.api.key}") String apiKey, @Value("${weather.api.url}") String apiUrl, ApplicationEventPublisher eventPublisher) {
        this.weatherRepository = weatherRepository;
        this.apiKey = apiKey;
        this.apiURL = apiUrl;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void fetchAndSaveCityWeather(String cityName) {
        String url = String.format(apiURL, cityName, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        if (response != null) {
            processWeatherResponse(response, cityName);
        } else {
            LOGGER.error("No response received for city: {}", cityName);
        }
    }

    private void processWeatherResponse(String response, String cityName) {
        try {
            JSONObject weatherData = new JSONObject(response);
            Weather weatherRecord = extractWeatherData(weatherData, cityName);

            if (weatherRecord != null && !isUpToDate(weatherRecord)) {
                weatherRepository.save(weatherRecord);
                LOGGER.info("Weather data saved for {} at {}", cityName, weatherRecord.getTime());
                eventPublisher.publishEvent(new WeatherUpdateEvent(this, convertToDTO(weatherRecord)));
            } else {
                LOGGER.info("Duplicate weather data for {} at {}, skipping save.", cityName, weatherRecord.getTime());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing weather data for {}: {}", cityName, e.getMessage());
        }
    }

    private Weather extractWeatherData(JSONObject weatherData, String cityName) {
        try {
            long timestamp = weatherData.getLong("dt");
            JSONObject main = weatherData.getJSONObject("main");
            double temperature = main.getDouble("temp");
            int humidity = main.getInt("humidity");
            JSONObject wind = weatherData.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            int windDirection = wind.getInt("deg");
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

    private boolean isUpToDate(Weather weatherRecord) {
        Optional<Weather> existingWeather = weatherRepository.findByCityAndTime(weatherRecord.getCity(), weatherRecord.getTime());
        return existingWeather.isPresent() && isDataPresent(existingWeather.get(), weatherRecord);
    }

    private boolean isDataPresent(Weather existingWeather, Weather newWeather) {
        return existingWeather.getTemperature().equals(newWeather.getTemperature())
                && existingWeather.getHumidity().equals(newWeather.getHumidity())
                && existingWeather.getWindSpeed().equals(newWeather.getWindSpeed())
                && existingWeather.getWindDirection().equals(newWeather.getWindDirection());
    }

    @Override
    public List<DTO> getWeather(String cityName) {
        return weatherRepository.findByCity(cityName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DTO convertToDTO(Weather weather) {
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
}
