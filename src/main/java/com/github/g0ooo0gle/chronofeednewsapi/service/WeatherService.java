package com.github.g0ooo0gle.chronofeednewsapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * wttr.in から天気情報を取得して WeatherData に変換するシンプルなサービス。
 * - JSON フォーマット: https://wttr.in/{location}?format=j1[&lang=xx]
 * - 取得失敗時は null を返す（呼び出し側でキャッシュやフォールバックを扱う）
 *
 * 言語設定（SettingsService）に従って、wttr.in へのリクエストに lang パラメータを付与します。
 */
@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SettingsService settingsService;

    public WeatherService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * 指定された地点の最新天気を取得する。
     * SettingsService に保存された言語設定を参照して、wttr.in に lang パラメータを渡します。
     *
     * @param location 地点名（例: Tokyo, Yokohama など）
     * @return WeatherData（取得失敗時は null）
     */
    public WeatherData fetchWeather(String location) {
        try {
            String encodedLoc = encodeLocation(location);
            String baseUrl = String.format("https://wttr.in/%s?format=j1", encodedLoc);

            // Prefer using Accept-Language header per wttr.in usage; fallback to no header if undefined
            String lang = settingsService != null ? settingsService.getLanguage() : null;
            HttpHeaders headers = new HttpHeaders();
            if (lang != null && !lang.isBlank()) {
                headers.set("Accept-Language", lang.trim());
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Build a debug-friendly request URL representation (lang as query param for visibility)
            String requestUrl = (lang == null || lang.isBlank())
                    ? baseUrl
                    : baseUrl + "&lang=" + URLEncoder.encode(lang.trim(), StandardCharsets.UTF_8);

            ResponseEntity<String> resp = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return null;
            }
            String body = resp.getBody();

            JsonNode root = objectMapper.readTree(body);

            // wttr.in の j1 JSON は current_condition 配列を持つ
            JsonNode current = root.path("current_condition").isArray() && root.path("current_condition").size() > 0
                    ? root.path("current_condition").get(0)
                    : null;

            String summary = "";
            String tempC = "";

            // 詳細フィールド（存在する場合は取得）
            String humidity = "";
            String cloudcover = "";
            String pressure = "";
            String windSpeedKmph = "";
            String windDir16Point = "";
            String observationTime = "";
            String localObsDateTime = "";
            String nearestAreaName = "";

            if (current != null) {
                // 優先: lang_ja の値があれば日本語表記を取得する
                JsonNode jaArr = current.path("lang_ja");
                if (jaArr.isArray() && jaArr.size() > 0) {
                    summary = jaArr.get(0).path("value").asText("");
                } else {
                    // それ以外は標準の weatherDesc を使う
                    JsonNode descArr = current.path("weatherDesc");
                    if (descArr.isArray() && descArr.size() > 0) {
                        summary = descArr.get(0).path("value").asText("");
                    } else {
                        summary = current.path("lang_en").asText("");
                    }
                }

                // 温度はフィールド名に差異があるため両方確認
                tempC = current.path("temp_C").asText("");
                if (tempC == null || tempC.isBlank()) {
                    tempC = current.path("tempC").asText("");
                }

                humidity = current.path("humidity").asText("");
                cloudcover = current.path("cloudcover").asText("");
                pressure = current.path("pressure").asText("");
                windSpeedKmph = current.path("windspeedKmph").asText("");
                windDir16Point = current.path("winddir16Point").asText("");
                observationTime = current.path("observation_time").asText("");
                localObsDateTime = current.path("localObsDateTime").asText("");
            }

            // nearest_area から最寄りの名前を取得（あれば）
            JsonNode nearest = root.path("nearest_area");
            if (nearest.isArray() && nearest.size() > 0) {
                JsonNode area = nearest.get(0).path("areaName");
                if (area.isArray() && area.size() > 0) {
                    nearestAreaName = area.get(0).path("value").asText("");
                }
            }

            WeatherData wd = new WeatherData(
                    location,
                    Instant.now(),
                    summary,
                    tempC,
                    body,
                    requestUrl,
                    humidity,
                    cloudcover,
                    pressure,
                    windSpeedKmph,
                    windDir16Point,
                    observationTime,
                    localObsDateTime,
                    nearestAreaName
            );
            return wd;
        } catch (Exception e) {
            // ログを出したいが簡潔にするため標準出力へ
            System.err.println("WeatherService.fetchWeather error for " + location + ": " + e.getMessage());
            return null;
        }
    }

    private String encodeLocation(String loc) {
        if (loc == null) return "";
        return URLEncoder.encode(loc, StandardCharsets.UTF_8);
    }
}
