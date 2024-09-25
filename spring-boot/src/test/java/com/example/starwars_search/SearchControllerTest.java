package com.example.starwars_search;

import com.example.starwars_search.controller.SearchController;
import com.example.starwars_search.dto.EntityDTO;
import com.example.starwars_search.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private SearchController searchController;

    private EntityDTO entityDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entityDTO = new EntityDTO();
    }

    @Test
    void testSearchOnline() throws Exception {
        String type = "planet";
        String name = "Tatooine";
        Boolean isOffline = false;

        when(searchService.searchByTypeAndName(type, name)).thenReturn(entityDTO);
        when(mapper.writeValueAsString(entityDTO)).thenReturn("{\"someField\":\"someValue\"}");

        ResponseEntity<EntityDTO> response = searchController.search(type, name, isOffline);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(entityDTO, response.getBody());
        verify(searchService).searchByTypeAndName(type, name);
        verify(mapper).writeValueAsString(entityDTO);
    }

    @Test
    void testSearchOffline() throws Exception {
        String type = "planet";
        String name = "Tatooine";
        Boolean isOffline = true;

        when(searchService.searchByTypeAndNameOffline(type, name)).thenReturn(entityDTO);
        when(mapper.writeValueAsString(entityDTO)).thenReturn("{\"someField\":\"someValue\"}");

        ResponseEntity<EntityDTO> response = searchController.search(type, name, isOffline);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(entityDTO, response.getBody());
        verify(searchService).searchByTypeAndNameOffline(type, name);
        verify(mapper).writeValueAsString(entityDTO);
    }

    @Test
    void testSearchError() throws Exception {
        String type = "planet";
        String name = "Tatooine";
        Boolean isOffline = false;

        when(searchService.searchByTypeAndName(type, name)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<EntityDTO> response = searchController.search(type, name, isOffline);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(searchService).searchByTypeAndName(type, name);
    }

    @Test
    void testSearchJsonProcessingException() throws Exception {
        String type = "planet";
        String name = "Tatooine";
        Boolean isOffline = false;

        when(searchService.searchByTypeAndName(type, name)).thenReturn(entityDTO);
        when(mapper.writeValueAsString(entityDTO)).thenThrow(new JsonProcessingException("JSON Error") {});

        ResponseEntity<EntityDTO> response = searchController.search(type, name, isOffline);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(entityDTO, response.getBody());
        verify(searchService).searchByTypeAndName(type, name);
        verify(mapper).writeValueAsString(entityDTO);
    }
}

