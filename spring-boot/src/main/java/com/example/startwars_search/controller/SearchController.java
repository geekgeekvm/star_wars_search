package com.example.startwars_search.controller;

import com.example.startwars_search.service.SearchService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public Object search(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam Boolean isOffline) {
        return isOffline ? searchService.searchByTypeAndNameOffline(type, name): searchService.searchByTypeAndName(type, name);
    }
}

