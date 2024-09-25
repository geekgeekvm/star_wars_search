package com.example.starwars_search.service;

import com.example.starwars_search.dto.EntityDTO;
import com.example.starwars_search.model.OfflineData;
import com.example.starwars_search.response.*;
import com.example.starwars_search.util.BaseEntity;
import com.example.starwars_search.util.FilmBaseEntity;
import com.example.starwars_search.util.GenericBaseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;


@Service
public class SearchService {

    @Value("${retryCount : 3}")
    private int retryCount;

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final RestTemplate restTemplate;
    private final Map<String, String> APIs = Map.of(
            "films", "https://swapi.dev/api/films/",
            "vehicles", "https://swapi.dev/api/vehicles/",
            "people", "https://swapi.dev/api/people/",
            "planets", "https://swapi.dev/api/planets/",
            "species", "https://swapi.dev/api/species/",
            "starships", "https://swapi.dev/api/starships/"
    );
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private Function<String, BaseResponse<?>> getStringBaseResponseFunction(String type) {
        Map<String, Function<String, BaseResponse<?>>> responseMap = new HashMap<>();
        responseMap.put("people", url -> restTemplate.getForObject(url, PeopleResponse.class));
        responseMap.put("planets", url -> restTemplate.getForObject(url, PlanetResponse.class));
        responseMap.put("species", url -> restTemplate.getForObject(url, SpeciesResponse.class));
        responseMap.put("starships", url -> restTemplate.getForObject(url, StarshipResponse.class));
        responseMap.put("vehicles", url -> restTemplate.getForObject(url, VehicleResponse.class));
        responseMap.put("films", url -> restTemplate.getForObject(url, FilmResponse.class));
        return responseMap.get(type.toLowerCase());
    }

    private boolean isFilm(String type) {
        return type.equalsIgnoreCase("films");
    }

    public SearchService(RestTemplate restTemplate, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    /*
    This method helps us search SWAPI in a generic way,
    so that irrespective of the object type we query for, we can get the response and map it to our output
     */
    public EntityDTO searchByTypeAndName(@NotNull String type, @NotNull String name) {
        String endpoint = APIs.get(type.toLowerCase());
        if (endpoint == null) {
            throw new IllegalArgumentException("Invalid type provided. Allowed types: films, vehicles, people, planets, species, starships.");
        }

        Function<String, BaseResponse<?>> responseFunction = getStringBaseResponseFunction(type);
        /* Retry the API call to make service more resilient in case of network / other recoverable issues.
           In case of failure, fetch data from stored SWAPI data instead.
         */
        BaseResponse<?> response = null;
        boolean doRetry = true;
        while ( retryCount > 0 && doRetry) {
            try {
                response = responseFunction.apply(endpoint);
                doRetry = false;
            } catch (Exception ex) {
               log.warn("Issue trying to fetch response from SWAPI, retrying.");
               retryCount -= 1;
            }
        }
        if ( retryCount == 0 ){
            log.error("Could not connect to SWAPI, fetching from stored data.");
            return searchByTypeAndNameOffline(type, name);
        }
        do {
            BaseEntity baseEntity = isFilm(type) ? new FilmBaseEntity(name, response) : new GenericBaseEntity(name, response, restTemplate);
            Optional<EntityDTO> result = baseEntity.getDTO();
            if (result.isPresent()) {
                return result.orElse(null);
            }
            if (response != null) {
                endpoint = response.getNext();
            }
            if (endpoint != null) {
                response = responseFunction.apply(endpoint);
            }
        } while (endpoint != null);
        return new EntityDTO();
    }

    /*
    This method helps search crawled SWAPI data in case of issue in fetching from SWAPI or if user specifically inputs isOffline flag
     */
    public EntityDTO searchByTypeAndNameOffline(@NotNull String type, @NotNull String name) {
        Resource resource = resourceLoader.getResource("classpath:data.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<OfflineData> data = objectMapper.readValue(inputStream, new TypeReference<>() {});
            Optional<EntityDTO> response =  data.stream()
                    .filter(item -> item.getType().equalsIgnoreCase(type) && item.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(item -> new EntityDTO(item.getCount(), item.getFilms()));
            return response.orElseThrow(() -> new NoSuchElementException("Not Found"));
        } catch (IOException | NoSuchElementException e) {
            log.warn("Issue parsing offline data or data does not exist.");
            return new EntityDTO();
        }
    }
}

