package com.example.startwars_search.util;

import com.example.startwars_search.dto.EntityDTO;
import com.example.startwars_search.model.Film;
import com.example.startwars_search.model.Entity;
import com.example.startwars_search.response.BaseResponse;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

public class GenericBaseEntity implements BaseEntity {
    private final String name;
    private final BaseResponse<?> response;
    private final RestTemplate restTemplate;

    public GenericBaseEntity(String name, BaseResponse<?> response, RestTemplate restTemplate) {
        this.name = name;
        this.response = response;
        this.restTemplate = restTemplate;
    }

    private CompletableFuture<String> getFilmTitleAsync(String filmUrl) {
        return CompletableFuture.supplyAsync(() -> {
            Film film = restTemplate.getForObject(filmUrl, Film.class);
            return film != null ? film.getTitle() : null;
        });
    }

    private List<String> getFilmsFromEntityAsync(Entity entity) {
        List<CompletableFuture<String>> futures = entity.getFilms()
                .stream()
                .map(this::getFilmTitleAsync)
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Optional<Entity> filter(BaseResponse<?> response, String name) {
        return response
                .getResults()
                .stream()
                .filter(Entity.class::isInstance)
                .map(Entity.class::cast)
                .filter(item -> item.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<EntityDTO> getDTO() {
        return filter(response, name).map(entity -> new EntityDTO(response.getCount(), getFilmsFromEntityAsync(entity)));
    }
}
