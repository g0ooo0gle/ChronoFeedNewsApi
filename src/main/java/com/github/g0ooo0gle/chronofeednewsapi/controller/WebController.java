package com.github.g0ooo0gle.chronofeednewsapi.controller;

import com.github.g0ooo0gle.chronofeednewsapi.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("items", itemRepository.findTop50ByOrderByPublishedDateDesc());
        return "index";
    }
}
