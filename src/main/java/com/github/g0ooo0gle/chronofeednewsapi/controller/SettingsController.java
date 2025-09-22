package com.github.g0ooo0gle.chronofeednewsapi.controller;

import com.github.g0ooo0gle.chronofeednewsapi.scheduler.WeatherScheduler;
import com.github.g0ooo0gle.chronofeednewsapi.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 設定ページ（簡易実装・メモリ保存）
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;
    private final WeatherScheduler weatherScheduler;

    public SettingsController(SettingsService settingsService, WeatherScheduler weatherScheduler) {
        this.settingsService = settingsService;
        this.weatherScheduler = weatherScheduler;
    }

    @GetMapping
    public String settingsPage(Model model) {
        List<String> current = settingsService.getLocations();
        String locationsCsv;
        if (current == null || current.isEmpty()) {
            // デフォルト値を画面に表示する（settings で編集可能にする）
            locationsCsv = String.join(",", List.of(
                    "Tokyo",
                    "Yokohama",
                    "Saitama",
                    "Chiba",
                    "Kawasaki",
                    "Sagamihara",
                    "Utsunomiya",
                    "Mito",
                    "Maebashi"
            ));
        } else {
            locationsCsv = String.join(",", current);
        }
        model.addAttribute("locations", locationsCsv);

        // 更新間隔を分単位で渡す（UIで編集しやすくする）
        long intervalMs = settingsService.getUpdateIntervalMs();
        long intervalMinutes = Math.max(1, intervalMs / 60000);
        model.addAttribute("updateIntervalMinutes", intervalMinutes);

        // 言語設定を渡す（デフォルトは SettingsService が持つ値）
        String lang = settingsService.getLanguage();
        model.addAttribute("language", lang);

        return "settings";
    }

    @PostMapping
    public String saveSettings(@RequestParam("locations") String locationsCsv,
                               @RequestParam(value = "updateIntervalMinutes", required = false) String updateIntervalMinutesStr,
                               @RequestParam(value = "language", required = false) String language,
                               @RequestParam(value = "force", required = false) String force) {
        List<String> list = Arrays.stream(locationsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        settingsService.setLocations(list);

        if (updateIntervalMinutesStr != null && !updateIntervalMinutesStr.isBlank()) {
            try {
                long minutes = Long.parseLong(updateIntervalMinutesStr.trim());
                if (minutes > 0) {
                    settingsService.setUpdateIntervalMs(minutes * 60_000L);
                }
            } catch (NumberFormatException ignored) {
                // 無効な入力は無視（既存値を維持）
            }
        }

        if (language != null && !language.isBlank()) {
            settingsService.setLanguage(language.trim());
        }

        // フォームから "force" が来たら即時非同期更新をトリガー
        if (force != null && !force.isBlank()) {
            try {
                weatherScheduler.runOnceAsync();
            } catch (Exception e) {
                System.err.println("Failed to trigger force update: " + e.getMessage());
            }
        }

        return "redirect:/settings";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<String> apiGet() {
        return settingsService.getLocations();
    }
}
