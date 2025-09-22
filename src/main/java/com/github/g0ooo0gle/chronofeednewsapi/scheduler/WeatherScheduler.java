package com.github.g0ooo0gle.chronofeednewsapi.scheduler;

import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import com.github.g0ooo0gle.chronofeednewsapi.service.WeatherCacheService;
import com.github.g0ooo0gle.chronofeednewsapi.service.WeatherService;
import com.github.g0ooo0gle.chronofeednewsapi.service.SettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 動的間隔に対応したスケジューラ実装。
 * SettingsService の updateIntervalMs を参照して、ループ毎に待機時間を変える。
 * 各地点間には短いウェイトを入れて外部 API への同時大量リクエストを避ける。
 */
@Component
public class WeatherScheduler {

    private final WeatherService weatherService;
    private final WeatherCacheService cacheService;
    private final SettingsService settingsService;

    // 取得対象のデフォルト地点（関東の主要都市）
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

    // 各地点間の短い遅延（ミリ秒）
    @Value("${weather.per.location.delay.ms:500}")
    private long perLocationDelayMs;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean running = true;

    public WeatherScheduler(WeatherService weatherService, WeatherCacheService cacheService, SettingsService settingsService) {
        this.weatherService = weatherService;
        this.cacheService = cacheService;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void start() {
        // 最初は即時実行、その後ループ内で settingsService.getUpdateIntervalMs() を参照して待機
        executor.execute(this::runLoop);
    }

    private void runLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
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
                    } catch (Exception e) {
                        System.err.println("WeatherScheduler error for " + loc + ": " + e.getMessage());
                    }

                    // 各リクエスト間に短い待ち（設定可能）
                    try {
                        Thread.sleep(Math.max(0, perLocationDelayMs));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // ループ完了後に、設定された更新間隔だけ待機
                long intervalMs = settingsService.getUpdateIntervalMs();
                if (intervalMs <= 0) intervalMs = 600_000L; // 安全余地: デフォルト 10分
                try {
                    TimeUnit.MILLISECONDS.sleep(intervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            } catch (Exception e) {
                System.err.println("WeatherScheduler main loop error: " + e.getMessage());
                try {
                    TimeUnit.SECONDS.sleep(30); // エラー発生時は短めに待つ
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * 即時更新を非同期で実行する。設定画面の「強制更新」ボタンから呼び出せる。
     * このメソッドはスケジューラ本体のループとは別に一回分だけ更新を行う。
     */
    public void runOnceAsync() {
        executor.execute(() -> {
            try {
                List<String> targets = settingsService.getLocations();
                if (targets == null || targets.isEmpty()) {
                    targets = defaultLocations;
                }

                for (String loc : targets) {
                    try {
                        WeatherData wd = weatherService.fetchWeather(loc);
                        if (wd != null) {
                            cacheService.put(loc, wd);
                            System.out.println("Force-updated weather for " + loc + " at " + wd.getFetchedAt());
                        } else {
                            System.err.println("Failed to fetch weather for " + loc + " (force update)");
                        }
                    } catch (Exception e) {
                        System.err.println("WeatherScheduler force update error for " + loc + ": " + e.getMessage());
                    }

                    // 各リクエスト間に短い待ち（設定可能）
                    try {
                        Thread.sleep(Math.max(0, perLocationDelayMs));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("WeatherScheduler runOnceAsync error: " + e.getMessage());
            }
        });
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
    }
}
