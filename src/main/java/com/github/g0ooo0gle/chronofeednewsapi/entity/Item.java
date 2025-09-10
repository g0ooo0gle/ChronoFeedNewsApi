package com.github.g0ooo0gle.chronofeednewsapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_published_date", columnList = "publishedDate"),
        @Index(name = "idx_link_unique", columnList = "link", unique = true)
})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 512, nullable = false, unique = true)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private Date publishedDate;

    @Column(length = 50, nullable = false)
    private String sourceType;

    @Column(columnDefinition = "TEXT")
    private String additionalAttributes;
}
