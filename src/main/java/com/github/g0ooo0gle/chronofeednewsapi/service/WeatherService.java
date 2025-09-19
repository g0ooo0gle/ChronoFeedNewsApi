package com.github.g0ooo0gle.chronofeednewsapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

/**
 * wttr.in から天気情報を取得して WeatherData に変換するシンプルなサービス。
 * - JSON フォーマット: https://wttr.in/{location}?format=j1
 * - 取得失敗時は null を返す（呼び出し側でキャッシュやフォールバックを扱う）
 */
@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 指定された地点の最新天気を取得する。
     * @param location 地点名（例: Tokyo, Yokohama など）
     * @return WeatherData（取得失敗時は null）
     */
    public WeatherData fetchWeather(String location) {
        try {
            String url = String.format("https://wttr.in/%s?format=j1", encodeLocation(location));
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
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

            if (current != null) {
                JsonNode descArr = current.path("weatherDesc");
                if (descArr.isArray() && descArr.size() > 0) {
                    summary = descArr.get(0).path("value").asText("");
                } else {
                    summary = current.path("lang_en").asText("");
                }
                tempC = current.path("temp_C").asText("");
            }

            WeatherData wd = new WeatherData(location, Instant.now(), summary, tempC, body);
            return wd;
        } catch (Exception e) {
            // ログを出したいが簡潔にするため標準出力へ
            System.err.println("WeatherService.fetchWeather error for " + location + ": " + e.getMessage());
            return null;
        }
    }

    private String encodeLocation(String loc) {
        // 簡易的にスペースを+に置換。複雑なエンコードは不要な想定
        return loc.replace(" ", "+");
    }
}
