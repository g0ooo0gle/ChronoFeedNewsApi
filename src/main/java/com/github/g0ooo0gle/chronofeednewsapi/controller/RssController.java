package com.github.g0ooo0gle.chronofeednewsapi.controller;

import com.github.g0ooo0gle.chronofeednewsapi.entity.Item;
import com.github.g0ooo0gle.chronofeednewsapi.repository.ItemRepository;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.io.WireFeedOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RssController {

    @Autowired
    private ItemRepository itemRepository;

    @Value("${rss.delivery.title}")
    private String rssTitle;

    @Value("${rss.delivery.description}")
    private String rssDescription;

    @Value("${rss.delivery.link}")
    private String rssLink;

    @GetMapping(value = "/feed.rss", produces = "application/rss+xml")
    public String getFeed() throws Exception {
        Channel channel = new Channel("rss_2.0");
        channel.setTitle(rssTitle);
        channel.setLink(rssLink);
        channel.setDescription(rssDescription);

        List<Item> items = itemRepository.findTop50ByOrderByPublishedDateDesc();
        List<com.rometools.rome.feed.rss.Item> rssItems = items.stream()
                .map(this::toRssItem)
                .collect(Collectors.toList());

        channel.setItems(rssItems);

        WireFeedOutput output = new WireFeedOutput();
        return output.outputString(channel);
    }

    private com.rometools.rome.feed.rss.Item toRssItem(Item item) {
        com.rometools.rome.feed.rss.Item rssItem = new com.rometools.rome.feed.rss.Item();
        rssItem.setTitle(item.getTitle());
        rssItem.setLink(item.getLink());
        rssItem.setPubDate(item.getPublishedDate());

        if (item.getSummary() != null) {
            Description description = new Description();
            description.setType("text/plain");
            description.setValue(item.getSummary());
            rssItem.setDescription(description);
        }

        return rssItem;
    }
}
