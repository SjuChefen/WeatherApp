package com.CPS.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocationController {

    @GetMapping("/")
    public String index() {
        return "locationPicker";
    }
}
