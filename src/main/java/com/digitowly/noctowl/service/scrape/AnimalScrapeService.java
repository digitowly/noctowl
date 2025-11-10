package com.digitowly.noctowl.service.scrape;

import com.digitowly.noctowl.service.wikipedia.dto.AnimalScrapeResult;
import com.digitowly.noctowl.service.wikipedia.AnimalWikipediaScrapeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimalScrapeService {

    private final AnimalWikipediaScrapeService animalWikipediaScrapeService;

    public AnimalScrapeResult scrape(String scientificName) {
        var infoBox = animalWikipediaScrapeService.scrapeInfobox(scientificName);
        return new AnimalScrapeResult(infoBox);
    }
}
