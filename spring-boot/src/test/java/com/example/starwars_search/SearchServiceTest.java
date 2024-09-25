package com.example.starwars_search;

import com.example.starwars_search.dto.EntityDTO;
import com.example.starwars_search.service.SearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set retryCount using ReflectionTestUtils
        ReflectionTestUtils.setField(searchService, "retryCount", 3);
    }

    @Test
    void testSearchByTypeAndName_InvalidTypeThrowsException() {
        // Arrange
        String invalidType = "invalid_type";
        String name = "Luke Skywalker";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                searchService.searchByTypeAndName(invalidType, name));

        assertEquals("Invalid type provided. Allowed types: films, vehicles, people, planets, species, starships.", exception.getMessage());
    }

    @Test
    void testSearchByTypeAndNameOffline_IOErrorHandling() throws Exception {
        // Arrange
        String type = "people";
        String name = "Luke Skywalker";
        Resource mockResource = mock(Resource.class);

        when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenThrow(new IOException("File not found"));

        // Act
        EntityDTO result = searchService.searchByTypeAndNameOffline(type, name);

        // Assert
        assertNotNull(result);  // Should return empty EntityDTO
        verify(objectMapper, never()).readValue(any(InputStream.class), any(TypeReference.class));
    }
}

