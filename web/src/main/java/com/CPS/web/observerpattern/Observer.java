package com.CPS.web.observerpattern;


import com.CPS.web.dto.DTO;

public interface Observer {
    void update(DTO weatherData);
}
