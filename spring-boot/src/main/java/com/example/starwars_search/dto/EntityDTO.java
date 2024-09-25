package com.example.starwars_search.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class EntityDTO {
    private int count;
    private List<String> films;

    public EntityDTO() {};

    public EntityDTO(int count, List<String> films) {
        this.count = count;
        this.films = films;
    }

}