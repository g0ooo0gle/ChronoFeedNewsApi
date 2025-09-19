package com.github.g0ooo0gle.chronofeednewsapi.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 簡易的な設定サービス（メモリ保持）
 * - 将来的にDB保存や外部設定に置き換え可能
 */
@Service
public class SettingsService {

    private final AtomicReference<List<String>> locationsRef = new AtomicReference<>(Collections.emptyList());

    /**
     * 監視対象の地点リストを取得する。
     * 空リストの場合はデフォルト地点を使用する想定。
     */
    public List<String> getLocations() {
        List<String> v = locationsRef.get();
        return v == null ? Collections.emptyList() : v;
    }

    /**
     * 監視対象の地点リストを設定する。
     */
    public void setLocations(List<String> locations) {
        if (locations == null) {
            locationsRef.set(Collections.emptyList());
        } else {
            locationsRef.set(List.copyOf(locations));
        }
    }
}
