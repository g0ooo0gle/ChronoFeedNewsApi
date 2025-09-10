package com.github.g0ooo0gle.chronofeednewsapi.model.newsapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsApiArticle {
    private String author;
    private String title;
    private String url;
    private String description;
    private String publishedAt;
    private NewsApiSource source;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsApiSource {
        private String id;
        private String name;
    }
}
