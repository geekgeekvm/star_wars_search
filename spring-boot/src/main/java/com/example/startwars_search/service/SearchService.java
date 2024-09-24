package com.example.startwars_search.service;

import com.example.startwars_search.dto.EntityDTO;
import com.example.startwars_search.model.OfflineData;
import com.example.startwars_search.response.*;
import com.example.startwars_search.util.BaseEntity;
import com.example.startwars_search.util.FilmBaseEntity;
import com.example.startwars_search.util.GenericBaseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;


@Service
public class SearchService {

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

    public Optional<EntityDTO> searchByTypeAndName(@NotNull String type, @NotNull String name) {
        String endpoint = APIs.get(type.toLowerCase());
        if (endpoint == null) {
            throw new IllegalArgumentException("Invalid type provided. Allowed types: films, vehicles, people, planets, species, starships.");
        }

        Function<String, BaseResponse<?>> responseFunction = getStringBaseResponseFunction(type);
        BaseResponse<?> response = responseFunction.apply(endpoint);
        BaseEntity baseEntity = isFilm(type) ? new FilmBaseEntity(name, response) : new GenericBaseEntity(name, response, restTemplate);
        do {
            Optional<EntityDTO> result = baseEntity.getDTO();
            if (result.isPresent()) {
                return result;
            }
            endpoint = response.getNext();
            if (endpoint != null) {
                response = responseFunction.apply(endpoint);
            }
        } while (endpoint != null);
        return Optional.empty();
    }

    public Optional<EntityDTO> searchByTypeAndNameOffline(@NotNull String type, @NotNull String name) {
        Resource resource = resourceLoader.getResource("classpath:data.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<OfflineData> data = objectMapper.readValue(inputStream, new TypeReference<List<OfflineData>>() {});
            return data.stream()
                    .filter(item -> item.getType().equalsIgnoreCase(type) && item.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(item -> new EntityDTO(item.getCount(), item.getFilms()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

