package com.github.g0ooo0gle.chronofeednewsapi.controller;

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

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public String settingsPage(Model model) {
        List<String> current = settingsService.getLocations();
        model.addAttribute("locations", String.join(",", current));
        return "settings";
    }

    @PostMapping
    public String saveSettings(@RequestParam("locations") String locationsCsv) {
        List<String> list = Arrays.stream(locationsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        settingsService.setLocations(list);
        return "redirect:/settings";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<String> apiGet() {
        return settingsService.getLocations();
    }
}
