package com.github.g0ooo0gle.chronofeednewsapi.model;

import java.time.Instant;

/**
 * シンプルな天気データ保持用モデル
 */
public class WeatherData {
    private String location;
    private Instant fetchedAt;
    private String summary;
    private String temperatureC;
    private String rawJson;

    public WeatherData() {}

    public WeatherData(String location, Instant fetchedAt, String summary, String temperatureC, String rawJson) {
        this.location = location;
        this.fetchedAt = fetchedAt;
        this.summary = summary;
        this.temperatureC = temperatureC;
        this.rawJson = rawJson;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(String temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "location='" + location + '\'' +
                ", fetchedAt=" + fetchedAt +
                ", summary='" + summary + '\'' +
                ", temperatureC='" + temperatureC + '\'' +
                '}';
    }
}
