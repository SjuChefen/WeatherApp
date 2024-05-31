package com.CPS.web.dto;

import com.CPS.web.models.Location;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class WeatherInfoDto {
    private Long id;
    private Location location;
    private Date date;
    private Double temperature;
    private Double precipitation;
    private Double humidity;
}
