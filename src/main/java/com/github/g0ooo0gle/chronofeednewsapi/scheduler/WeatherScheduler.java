package com.github.g0ooo0gle.chronofeednewsapi.scheduler;

import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import com.github.g0ooo0gle.chronofeednewsapi.service.WeatherCacheService;
import com.github.g0ooo0gle.chronofeednewsapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * シンプルなスケジューラ。定期的に関東主要地点の天気を取得してキャッシュに保存する。
 * - 更新間隔は application.properties の weather.update.interval.ms で指定（デフォルト 1800000ms = 30分）
 * - 各地点間に短いウェイトを入れて外部 API への同時大量リクエストを避ける
 */
@Component
public class WeatherScheduler {

    private final WeatherService weatherService;
    private final WeatherCacheService cacheService;
    private final com.github.g0ooo0gle.chronofeednewsapi.service.SettingsService settingsService;

    // デフォルト更新間隔（ミリ秒）
    @Value("${weather.update.interval.ms:1800000}")
    private long updateIntervalMs;

    // 取得対象のデフォルト地点（関東の主要都市）
    // 後で設定ページで編集できるように拡張可能。SettingsService が返す値がなければこれを使う。
    private final List<String> defaultLocations = List.of(
            "Tokyo",
            "Yokohama",
            "Saitama",
            "Chiba",
            "Kawasaki",
            "Sagamihara",
            "Utsunomiya",
            "Mito",
            "Maebashi"
    );

    public WeatherScheduler(WeatherService weatherService, WeatherCacheService cacheService, com.github.g0ooo0gle.chronofeednewsapi.service.SettingsService settingsService) {
        this.weatherService = weatherService;
        this.cacheService = cacheService;
        this.settingsService = settingsService;
    }

        // fixedDelayString を使用してプロパティで間隔を制御
    @Scheduled(fixedDelayString = "${weather.update.interval.ms:1800000}")
    public void fetchAll() {
        // 設定サービスから地点リストを取得。空なら defaultLocations を使う
        List<String> targets = settingsService.getLocations();
        if (targets == null || targets.isEmpty()) {
            targets = defaultLocations;
        }

        for (String loc : targets) {
            try {
                WeatherData wd = weatherService.fetchWeather(loc);
                if (wd != null) {
                    cacheService.put(loc, wd);
                    System.out.println("Updated weather for " + loc + " at " + wd.getFetchedAt());
                } else {
                    System.err.println("Failed to fetch weather for " + loc);
                }
                // 各リクエスト間に短い待ち（500ms）。wttr.in への負荷を抑える目的。
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.err.println("WeatherScheduler interrupted");
            } catch (Exception e) {
                System.err.println("WeatherScheduler error for " + loc + ": " + e.getMessage());
            }
        }
    }
}
