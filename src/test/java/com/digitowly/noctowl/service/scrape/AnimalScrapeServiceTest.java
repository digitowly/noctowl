package com.digitowly.noctowl.service.scrape;

import com.digitowly.noctowl.service.scrape.dto.AnimalScrapeResult;
import com.digitowly.noctowl.service.scrape.dto.WikipediaInfobox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimalScrapeServiceTest {

    private AnimalWikipediaScrapeService animalWikipediaScrapeService;
    private AnimalScrapeService animalScrapeService;

    @BeforeEach
    void setUp() {
        this.animalWikipediaScrapeService = mock(AnimalWikipediaScrapeService.class);
        this.animalScrapeService = new AnimalScrapeService(animalWikipediaScrapeService);
    }

    @Test
    void scrape() {
        var scientificName = "Strix aluco";
        var expectedResult = AnimalScrapeResult.builder()
                .wikipediaInfobox(WikipediaInfobox.builder()
                        .scientificName(scientificName)
                        .build())
                .build();

        when(animalWikipediaScrapeService.scrapeInfobox(scientificName))
                .thenReturn(expectedResult.wikipediaInfobox());

        var result = animalScrapeService.scrape(scientificName);
        assertEquals(expectedResult, result);
    }
}