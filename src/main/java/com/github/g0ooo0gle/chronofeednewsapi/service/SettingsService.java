package com.github.g0ooo0gle.chronofeednewsapi.service;

import com.github.g0ooo0gle.chronofeednewsapi.entity.SettingsEntity;
import com.github.g0ooo0gle.chronofeednewsapi.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 設定サービス（DB 永続化版）
 *
 * - SettingsEntity を単一行（id=1）で保持する想定
 * - 起動時に存在しなければデフォルト値を返す
 * - 変更はその場で DB に保存される
 */
@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    // デフォルト値
    private static final List<String> DEFAULT_LOCATIONS = List.of(
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
    private static final long DEFAULT_INTERVAL_MS = 600_000L; // 10分
    private static final String DEFAULT_LANGUAGE = "ja";
    private static final Long SETTINGS_ID = 1L;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    private SettingsEntity loadOrCreate() {
        return settingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> {
                    SettingsEntity e = new SettingsEntity(SETTINGS_ID,
                            String.join(",", DEFAULT_LOCATIONS),
                            DEFAULT_INTERVAL_MS,
                            DEFAULT_LANGUAGE);
                    return settingsRepository.save(e);
                });
    }

    public List<String> getLocations() {
        SettingsEntity e = settingsRepository.findById(SETTINGS_ID).orElse(null);
        if (e == null || e.getLocations() == null || e.getLocations().isBlank()) {
            return DEFAULT_LOCATIONS;
        }
        return Arrays.stream(e.getLocations().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public void setLocations(List<String> locations) {
        SettingsEntity e = loadOrCreate();
        if (locations == null || locations.isEmpty()) {
            e.setLocations(String.join(",", DEFAULT_LOCATIONS));
        } else {
            e.setLocations(String.join(",", locations));
        }
        settingsRepository.save(e);
    }

    public long getUpdateIntervalMs() {
        SettingsEntity e = settingsRepository.findById(SETTINGS_ID).orElse(null);
        if (e == null || e.getUpdateIntervalMs() == null) return DEFAULT_INTERVAL_MS;
        return e.getUpdateIntervalMs();
    }

    @Transactional
    public void setUpdateIntervalMs(Long intervalMs) {
        if (intervalMs == null || intervalMs <= 0) return;
        SettingsEntity e = loadOrCreate();
        e.setUpdateIntervalMs(intervalMs);
        settingsRepository.save(e);
    }

    public String getLanguage() {
        SettingsEntity e = settingsRepository.findById(SETTINGS_ID).orElse(null);
        if (e == null || e.getLanguage() == null || e.getLanguage().isBlank()) return DEFAULT_LANGUAGE;
        return e.getLanguage();
    }

    @Transactional
    public void setLanguage(String lang) {
        if (lang == null || lang.isBlank()) return;
        SettingsEntity e = loadOrCreate();
        e.setLanguage(lang.trim());
        settingsRepository.save(e);
    }
}
