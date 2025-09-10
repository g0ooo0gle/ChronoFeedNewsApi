package com.github.g0ooo0gle.chronofeednewsapi.repository;

import com.github.g0ooo0gle.chronofeednewsapi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findTop50ByOrderByPublishedDateDesc();

    List<Item> findByLinkIn(List<String> links);
}
