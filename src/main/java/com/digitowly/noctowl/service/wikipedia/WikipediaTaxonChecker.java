package com.digitowly.noctowl.service.wikipedia;

import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.wikipedia.dto.WikipediaInfobox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class WikipediaTaxonChecker {

    private final AnimalWikipediaScrapeService animalWikipediaScrapeService;

    public boolean isTaxon(TaxonType type, String name) {
        return switch (type) {
            case ANIMAL -> isAnimal(name);
            case PLANT -> isPlant(name);
        };
    }

    private boolean isAnimal(String scientificName) {
        return Optional.ofNullable(animalWikipediaScrapeService.scrapeInfobox(scientificName))
                .flatMap(WikipediaInfobox::taxonomy)
                .flatMap(WikipediaInfobox.Taxonomy::kingdom)
                .map(k -> "Animalia".equals(k.scientificName()))
                .orElse(false);
    }

    private boolean isPlant(String scientificName) {
        // TODO: Implement plant checker
        log.warn("Plant checker not implemented yet");
        log.warn("Scientific name: {} will always return false", scientificName);
        return false;
    }
}
