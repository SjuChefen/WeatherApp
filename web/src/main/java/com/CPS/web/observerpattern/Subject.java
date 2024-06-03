package com.CPS.web.observerpattern;


import com.CPS.web.dto.DTO;

public interface Subject {
    void notifyObservers(DTO weatherData);
}
