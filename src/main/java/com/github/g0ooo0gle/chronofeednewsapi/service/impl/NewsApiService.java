package com.github.g0ooo0gle.chronofeednewsapi.service.impl;

import com.github.g0ooo0gle.chronofeednewsapi.entity.Item;
import com.github.g0ooo0gle.chronofeednewsapi.model.newsapi.NewsApiArticle;
import com.github.g0ooo0gle.chronofeednewsapi.model.newsapi.NewsApiResponse;
import com.github.g0ooo0gle.chronofeednewsapi.repository.ItemRepository;
import com.github.g0ooo0gle.chronofeednewsapi.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("newsApiService")
public class NewsApiService implements DataSourceService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Value("${data.sources.news-api.url}")
    private String apiUrl;

    @Value("${data.sources.news-api.api.key}")
    private String apiKey;

    @Value("${data.sources.news-api.type}")
    private String type;

    @Override
    public void fetchData() {
        String url = apiUrl + "&apiKey=" + apiKey;
        NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);

        if (response != null && response.getArticles() != null) {
            List<String> links = response.getArticles().stream()
                    .map(NewsApiArticle::getUrl)
                    .collect(Collectors.toList());

            List<String> existingLinks = itemRepository.findByLinkIn(links).stream()
                    .map(Item::getLink)
                    .collect(Collectors.toList());

            List<Item> newItems = response.getArticles().stream()
                    .filter(article -> !existingLinks.contains(article.getUrl()) && article.getTitle() != null && article.getUrl() != null)
                    .map(this::convertToItem)
                    .collect(Collectors.toList());

            itemRepository.saveAll(newItems);
        }
    }

    private Item convertToItem(NewsApiArticle article) {
        Item item = new Item();
        item.setTitle(article.getTitle());
        item.setLink(article.getUrl());
        item.setSummary(article.getDescription());
        item.setSourceType(getType());
        if (article.getPublishedAt() != null) {
            item.setPublishedDate(Date.from(ZonedDateTime.parse(article.getPublishedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant()));
        } else {
            item.setPublishedDate(new Date());
        }
        return item;
    }

    @Override
    public String getType() {
        return type;
    }
}
