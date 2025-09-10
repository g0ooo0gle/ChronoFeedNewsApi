package com.github.g0ooo0gle.chronofeednewsapi.service.impl;

import com.github.g0ooo0gle.chronofeednewsapi.entity.Item;
import com.github.g0ooo0gle.chronofeednewsapi.repository.ItemRepository;
import com.github.g0ooo0gle.chronofeednewsapi.service.DataSourceService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service("rssFeedService")
public class RssFeedService implements DataSourceService {

    @Autowired
    private ItemRepository itemRepository;

    @Value("${data.sources.rss-feed.url}")
    private String feedUrl;

    @Value("${data.sources.rss-feed.type}")
    private String type;

    @Override
    public void fetchData() {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
            List<SyndEntry> entries = feed.getEntries();

            List<String> links = entries.stream()
                    .map(SyndEntry::getLink)
                    .collect(Collectors.toList());

            List<String> existingLinks = itemRepository.findByLinkIn(links).stream()
                    .map(Item::getLink)
                    .collect(Collectors.toList());

            List<Item> newItems = entries.stream()
                    .filter(entry -> !existingLinks.contains(entry.getLink()))
                    .map(this::convertToItem)
                    .collect(Collectors.toList());

            itemRepository.saveAll(newItems);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Item convertToItem(SyndEntry entry) {
        Item item = new Item();
        item.setTitle(entry.getTitle());
        item.setLink(entry.getLink());
        if (entry.getDescription() != null) {
            item.setSummary(entry.getDescription().getValue());
        }
        item.setPublishedDate(entry.getPublishedDate());
        item.setSourceType(getType());
        return item;
    }

    @Override
    public String getType() {
        return type;
    }
}
