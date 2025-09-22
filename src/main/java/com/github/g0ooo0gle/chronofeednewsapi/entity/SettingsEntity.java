package com.github.g0ooo0gle.chronofeednewsapi.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * アプリ設定を1行で保持するシンプルなエンティティ。
 * - id は常に 1 を利用する想定（単一レコードで運用）
 * - locations: カンマ区切りの地点リスト
 * - updateIntervalMs: 更新間隔（ミリ秒）
 * - language: 言語コード（例: "ja", "en"）
 */
@Entity
@Table(name = "app_settings")
public class SettingsEntity {

    @Id
    private Long id = 1L;

    @Column(name = "locations", length = 2000)
    private String locations;

    @Column(name = "update_interval_ms")
    private Long updateIntervalMs;

    @Column(name = "language", length = 10)
    private String language;

    public SettingsEntity() {}

    public SettingsEntity(Long id, String locations, Long updateIntervalMs, String language) {
        this.id = id;
        this.locations = locations;
        this.updateIntervalMs = updateIntervalMs;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public Long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    public void setUpdateIntervalMs(Long updateIntervalMs) {
        this.updateIntervalMs = updateIntervalMs;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingsEntity)) return false;
        SettingsEntity that = (SettingsEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
