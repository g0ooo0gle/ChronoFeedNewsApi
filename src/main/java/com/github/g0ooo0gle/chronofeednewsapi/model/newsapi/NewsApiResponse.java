package com.github.g0ooo0gle.chronofeednewsapi.model.newsapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<NewsApiArticle> articles;
}
