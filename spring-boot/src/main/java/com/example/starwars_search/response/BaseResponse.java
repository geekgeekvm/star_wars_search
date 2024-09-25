package com.example.starwars_search.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
    private int count;
    private String next;
    private String previous;
    private List<T> results;
}