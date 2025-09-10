package com.github.g0ooo0gle.chronofeednewsapi.model.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    private WeatherMain main;
    private List<Weather> weather;
    private String name;
}
