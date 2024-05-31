package com.CPS.web.service;

import com.CPS.web.models.Location;
import com.CPS.web.models.WeatherInfo;
import com.CPS.web.repository.LocationRepository;
import com.CPS.web.repository.WeatherInfoRepository;
import com.CPS.web.util.CityByLocation;
import com.CPS.web.util.WeatherInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

@Service
public class WeatherInfoService {
    private final WeatherInfoRepository weatherInfoRepository;
    private final LocationRepository locationRepository;

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherInfoService(WeatherInfoRepository weatherInfoRepository, LocationRepository locationRepository, RestTemplate restTemplate) {
        this.weatherInfoRepository = weatherInfoRepository;
        this.locationRepository = locationRepository;
        this.restTemplate = restTemplate;
    }

    public String getWeatherData(String city) {
        // Get coordinates for the city
        String geoUrl = UriComponentsBuilder.fromHttpUrl("http://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", city)
                .queryParam("limit", 1)
                .queryParam("appid", apiKey)
                .toUriString();

        CityByLocation[] geoResponse = restTemplate.getForObject(geoUrl, CityByLocation[].class);
        if (geoResponse != null && geoResponse.length > 0) {
            CityByLocation cityByLocation = geoResponse[0];
            double lat = cityByLocation.getLat();
            double lon = cityByLocation.getLon();

            // Fetch weather data using coordinates
            String weatherUrl = UriComponentsBuilder.fromHttpUrl("http://api.openweathermap.org/data/2.5/weather")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", apiKey)
                    .toUriString();

            // Fetch weather data as String for model attribute
            String weatherData = restTemplate.getForObject(weatherUrl, String.class);

            // Fetch weather data as JSON object
            WeatherInfoResponse weatherResponse = restTemplate.getForObject(weatherUrl, WeatherInfoResponse.class);

            if (weatherResponse != null) {
                // Save location if not exists
                Location location = locationRepository.findByNameAndCountry(cityByLocation.getName(), weatherResponse.getSys().getCountry())
                        .orElse(Location.builder()
                                .name(cityByLocation.getName())
                                .latitude(cityByLocation.getLat())
                                .longitude(cityByLocation.getLon())
                                .country(weatherResponse.getSys().getCountry())
                                .build());
                locationRepository.save(location);

                // Create WeatherInfo object
                WeatherInfo weatherInfo = WeatherInfo.builder()
                        .location(location)
                        .date(new Date(weatherResponse.getDt() * 1000L)) // Convert UNIX timestamp to Date
                        .temperature(weatherResponse.getMain().getTemp())
                        .precipitation((double) weatherResponse.getClouds().getAll()) // Cast int to double
                        .humidity((double) weatherResponse.getMain().getHumidity()) // Cast int to double
                        .build();

                // Save WeatherInfo object
                weatherInfoRepository.save(weatherInfo);
            }

            return weatherData;
        }
        return "Location not found";
    }
}
