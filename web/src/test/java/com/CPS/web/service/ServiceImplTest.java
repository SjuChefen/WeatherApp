package com.CPS.web.service;

import com.CPS.web.dto.DTO;
import com.CPS.web.exceptions.CityNotFoundException;
import com.CPS.web.models.Weather;
import com.CPS.web.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;

import com.CPS.web.services.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceImplTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchAndSaveCityWeather_Success() {
        String cityName = "Odense";
        String response = "{ \"dt\": 1621369800, \"main\": { \"temp\": 15.5, \"humidity\": 60 }, \"wind\": { \"speed\": 3.5, \"deg\": 150 }, \"name\": \"Odense\", \"weather\": [{ \"icon\": \"01d\" }] }";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(response);
        when(weatherRepository.findByCityAndTime(anyString(), anyLong())).thenReturn(Optional.empty());
        when(weatherRepository.save(any(Weather.class))).thenReturn(new Weather());

        service.fetchAndSaveCityWeather(cityName);

        verify(weatherRepository, times(1)).save(any(Weather.class));
    }

    @Test
    public void testFetchAndSaveCityWeather_CityNotFound() {
        String cityName = "InvalidCity";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new CityNotFoundException(cityName + " not found."));

        assertThrows(CityNotFoundException.class, () -> service.fetchAndSaveCityWeather(cityName));

        verify(weatherRepository, never()).save(any(Weather.class));
    }

    @Test
    public void testGetWeather() {
        String cityName = "Odense";
        Weather weather = Weather.builder().city(cityName).time(1621369800L).temperature(15.5).humidity(60).build();
        when(weatherRepository.findByCity(cityName)).thenReturn(Collections.singletonList(weather));

        List<DTO> weatherData = service.getWeather(cityName);

        assertEquals(1, weatherData.size());
        assertEquals(cityName, weatherData.get(0).getCity());
    }
}