package com.CPS.web.controller;

import com.CPS.web.dto.DTO;
import com.CPS.web.exceptions.CityNotFoundException;
import com.CPS.web.services.IService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.CPS.web.controllers.WeatherDataController;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WeatherDataControllerTest {

    @Mock
    private IService service;

    @InjectMocks
    private WeatherDataController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetWeatherData_Success() throws Exception {
        String cityName = "Odense";
        DTO weatherData = DTO.builder().city(cityName).time(1621369800L).temperature(15.5).humidity(60).build();
        when(service.getWeather(cityName)).thenReturn(Collections.singletonList(weatherData));

        mockMvc.perform(get("/api/weather")
                        .param("city", cityName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value(cityName))
                .andExpect(jsonPath("$[0].temperature").value(15.5));

        verify(service, times(1)).getWeather(cityName);
    }

    @Test
    public void testGetWeatherData_CityNotFound() throws Exception {
        String cityName = "InvalidCity";
        when(service.getWeather(cityName)).thenThrow(new CityNotFoundException(cityName + " not found."));

        mockMvc.perform(get("/api/weather")
                        .param("city", cityName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(cityName + " not found."));

        verify(service, times(1)).getWeather(cityName);
    }
}