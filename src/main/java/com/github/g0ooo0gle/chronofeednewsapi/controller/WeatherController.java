package com.github.g0ooo0gle.chronofeednewsapi.controller;

import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import com.github.g0ooo0gle.chronofeednewsapi.service.WeatherCacheService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * 天気情報の表示用コントローラと API エンドポイント
 */
@Controller
public class WeatherController {

    private final WeatherCacheService cacheService;

    public WeatherController(WeatherCacheService cacheService) {
        this.cacheService = cacheService;
    }

    // Thymeleaf テンプレートを返すダッシュボード
    @GetMapping("/weather")
    public String weatherPage() {
        return "weather";
    }

    // キャッシュに溜まっている全地点の天気を JSON で返す
    @GetMapping("/api/weather")
    @ResponseBody
    public Collection<WeatherData> apiWeather() {
        return cacheService.getAll();
    }
}
