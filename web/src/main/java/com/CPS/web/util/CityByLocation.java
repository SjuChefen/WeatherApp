package com.CPS.web.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityByLocation {
    private String name;
    private double lat;
    private double lon;
}
