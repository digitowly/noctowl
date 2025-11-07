package com.digitowly.noctowl.service;

import com.digitowly.noctowl.model.SpeciesResponse;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.scrape.AnimalScrapeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpeciesService {

    private TaxonomyService taxonomyService;
    private AnimalScrapeService animalScrapeService;

    public SpeciesResponse findByUnspecificName(
            String name,
            String type,
            String lang
    ) {
        var langType = LanguageType.valueOf(lang.toUpperCase());
        var taxonType = getTaxonTypeFromPathVariable(type);

        var response = taxonomyService.find(taxonType, name, 1, langType);
        var taxonomy = response.entries().getFirst();
        var animalScrape = animalScrapeService.scrape(taxonomy.wikipedia().scientificName());

        return new SpeciesResponse(taxonomy, animalScrape);
    }

    private static TaxonType getTaxonTypeFromPathVariable(String pathVariable) {
        return switch (pathVariable) {
            case "plant", "plants" -> TaxonType.PLANT;
            case "animal", "animals" -> TaxonType.ANIMAL;
            default -> null;
        };
    }
}
