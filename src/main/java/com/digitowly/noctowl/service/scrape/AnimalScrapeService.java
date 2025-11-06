package com.digitowly.noctowl.service.scrape;

import com.digitowly.noctowl.service.scrape.dto.AnimalScrapeResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimalScrapeService {

    private AnimalWikipediaScrapeService animalWikipediaScrapeService;

    public AnimalScrapeResult scrape(String scientificName) {
        var infoBox = animalWikipediaScrapeService.scrapeInfobox(scientificName);

        return AnimalScrapeResult.builder()
                .wikipediaInfobox(infoBox)
                .build();
    }
}
