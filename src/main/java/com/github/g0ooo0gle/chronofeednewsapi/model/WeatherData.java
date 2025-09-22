package com.github.g0ooo0gle.chronofeednewsapi.model;

import java.time.Instant;

/**
 * 拡張した天気データ保持用モデル
 */
public class WeatherData {
    private String location;
    private Instant fetchedAt;
    private String summary;
    private String temperatureC;
    private String rawJson;
    private String requestUrl;

    // 追加フィールド
    private String humidity;
    private String cloudcover;
    private String pressure;
    private String windSpeedKmph;
    private String windDir16Point;
    private String observationTime;
    private String localObsDateTime;
    private String nearestAreaName;

    public WeatherData() {}

    public WeatherData(
            String location,
            Instant fetchedAt,
            String summary,
            String temperatureC,
            String rawJson,
            String requestUrl,
            String humidity,
            String cloudcover,
            String pressure,
            String windSpeedKmph,
            String windDir16Point,
            String observationTime,
            String localObsDateTime,
            String nearestAreaName
    ) {
        this.location = location;
        this.fetchedAt = fetchedAt;
        this.summary = summary;
        this.temperatureC = temperatureC;
        this.rawJson = rawJson;
        this.requestUrl = requestUrl;
        this.humidity = humidity;
        this.cloudcover = cloudcover;
        this.pressure = pressure;
        this.windSpeedKmph = windSpeedKmph;
        this.windDir16Point = windDir16Point;
        this.observationTime = observationTime;
        this.localObsDateTime = localObsDateTime;
        this.nearestAreaName = nearestAreaName;
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(String cloudcover) {
        this.cloudcover = cloudcover;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWindSpeedKmph() {
        return windSpeedKmph;
    }

    public void setWindSpeedKmph(String windSpeedKmph) {
        this.windSpeedKmph = windSpeedKmph;
    }

    public String getWindDir16Point() {
        return windDir16Point;
    }

    public void setWindDir16Point(String windDir16Point) {
        this.windDir16Point = windDir16Point;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }

    public String getLocalObsDateTime() {
        return localObsDateTime;
    }

    public void setLocalObsDateTime(String localObsDateTime) {
        this.localObsDateTime = localObsDateTime;
    }

    public String getNearestAreaName() {
        return nearestAreaName;
    }

    public void setNearestAreaName(String nearestAreaName) {
        this.nearestAreaName = nearestAreaName;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "location='" + location + '\'' +
                ", fetchedAt=" + fetchedAt +
                ", summary='" + summary + '\'' +
                ", temperatureC='" + temperatureC + '\'' +
                ", humidity='" + humidity + '\'' +
                ", cloudcover='" + cloudcover + '\'' +
                ", pressure='" + pressure + '\'' +
                ", windSpeedKmph='" + windSpeedKmph + '\'' +
                ", windDir16Point='" + windDir16Point + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                '}';
    }
}
