package com.example.starwars_search.controller;

import com.example.starwars_search.dto.EntityDTO;
import com.example.starwars_search.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SearchController {

    private final SearchService searchService;
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final ObjectMapper mapper;

    public SearchController(SearchService searchService, ObjectMapper mapper) {
        this.searchService = searchService;
        this.mapper = mapper;
    }

    /*
        This API helps us search SWAPI if it is online, else will search our scraped data.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public ResponseEntity<EntityDTO> search(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam Boolean isOffline) {

        EntityDTO response = null;
        log.info("Search API invoked with parameters Type : {} Name : {} and isOffline flag : {}", type, name, isOffline);

        try {
           if (isOffline) {
               response = searchService.searchByTypeAndNameOffline(type, name);
           } else {
               response = searchService.searchByTypeAndName(type, name);
           }
        } catch (Exception ex) {
            log.error("Error in searching Star Wars", ex);
        }

        try {
            log.info("Response {}", mapper.writeValueAsString(response));
        } catch ( JsonProcessingException e) {
            log.warn("Unable to parse output.");
        }

        return new ResponseEntity<>(response, response == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }
}

