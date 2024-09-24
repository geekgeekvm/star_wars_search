package com.example.startwars_search.model;

import lombok.Data;
import java.util.List;

@Data
public class OfflineData {
    private int count;
    private String name;
    private List<String> films;
    private String type;
}