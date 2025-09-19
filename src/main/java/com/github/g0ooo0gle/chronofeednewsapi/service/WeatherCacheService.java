package com.github.g0ooo0gle.chronofeednewsapi.service;

import com.github.g0ooo0gle.chronofeednewsapi.model.WeatherData;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * アプリ内メモリキャッシュ（スレッドセーフ）
 */
@Service
public class WeatherCacheService {

    private final ConcurrentHashMap<String, WeatherData> cache = new ConcurrentHashMap<>();

    public void put(String location, WeatherData data) {
        if (location == null || data == null) return;
        cache.put(location, data);
    }

    public WeatherData get(String location) {
        return cache.get(location);
    }

    public Collection<WeatherData> getAll() {
        return cache.values();
    }

    public void clear() {
        cache.clear();
    }
}
