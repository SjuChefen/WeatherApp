package com.CPS.web.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "weather", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"city", "time"})
})
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private Long time;
    private Double temperature;
    private Integer humidity;
    private Double windSpeed;
    private Integer windDirection;
    private String location;
    private String icon; // Add this line
}
