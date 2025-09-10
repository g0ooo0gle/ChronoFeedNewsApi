package com.github.g0ooo0gle.chronofeednewsapi.model.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherMain {
    private double temp;
}
