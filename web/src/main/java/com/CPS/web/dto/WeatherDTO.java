package com.CPS.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDTO {
    private String city;
    private Long time;
    private Double temperature;
    private Integer humidity;
    private Double windSpeed;
    private Integer windDirection;
    private String location;
    private String icon; // Add this line
}
