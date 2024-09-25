package com.example.starwars_search.util;

import com.example.starwars_search.dto.EntityDTO;
import com.example.starwars_search.model.Film;
import com.example.starwars_search.model.Entity;
import com.example.starwars_search.response.BaseResponse;

import java.util.Collections;
import java.util.Optional;

public class FilmBaseEntity implements BaseEntity {

    private final String name;
    private final BaseResponse<?> response;

    public FilmBaseEntity(String name, BaseResponse<?> response) {
        this.name = name;
        this.response = response;
    }

    private Optional<Film> filter(BaseResponse<?> response, String name) {
        return response
                .getResults()
                .stream()
                .filter(Entity.class::isInstance)
                .map(Film.class::cast)
                .filter(item -> item.getTitle().equals(name))
                .findFirst();
    }

    @Override
    public Optional<EntityDTO> getDTO() {
        return filter(response, name).map(
                film -> new EntityDTO(response.getCount(), Collections.singletonList(film.getTitle()))
        );
    }
}
