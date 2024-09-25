package com.example.starwars_search.util;

import com.example.starwars_search.dto.EntityDTO;
import java.util.Optional;

public interface BaseEntity {
    public Optional<EntityDTO> getDTO();
}
