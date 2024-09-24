package com.example.startwars_search.util;

import com.example.startwars_search.dto.EntityDTO;
import java.util.Optional;

public interface BaseEntity {
    public Optional<EntityDTO> getDTO();
}
