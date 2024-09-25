package com.example.starwars_search.model;

import lombok.Data;
import java.util.List;

@Data
public class OfflineData {
    private int count;
    private String name;
    private List<String> films;
    private String type;

    public OfflineData(String people, String lukeSkywalker, int i, List<String> aNewHope) {
        this.count = count;
        this.name = name;
        this.films = films;
        this.type = type;
    }
}