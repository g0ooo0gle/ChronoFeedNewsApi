package com.github.g0ooo0gle.chronofeednewsapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.g0ooo0gle.chronofeednewsapi.entity.Item;
import com.github.g0ooo0gle.chronofeednewsapi.model.openweathermap.WeatherApiResponse;
import com.github.g0ooo0gle.chronofeednewsapi.repository.ItemRepository;
import com.github.g0ooo0gle.chronofeednewsapi.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;

@Service("weatherApiService")
public class WeatherApiService implements DataSourceService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${data.sources.weather-api.url}")
    private String apiUrl;

    @Value("${data.sources.weather-api.api.key}")
    private String apiKey;

    @Value("${data.sources.weather-api.type}")
    private String type;

    @Override
    public void fetchData() {
        String url = apiUrl + "&appid=" + apiKey;
        WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

        if (response != null) {
            Item item = new Item();
            item.setTitle("Weather in " + response.getName() + ": " + response.getWeather().get(0).getMain());
            // Weather data doesn't have a natural unique link, so we generate one to satisfy the schema.
            // This means we will always insert a new record.
            item.setLink("urn:uuid:" + UUID.randomUUID());
            item.setSummary("Current temperature: " + response.getMain().getTemp() + "°C. " + response.getWeather().get(0).getDescription());
            item.setPublishedDate(new Date());
            item.setSourceType(getType());

            try {
                item.setAdditionalAttributes(objectMapper.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                // Log the error or handle it
                e.printStackTrace();
            }

            itemRepository.save(item);
        }
    }

    @Override
    public String getType() {
        return type;
    }
}
