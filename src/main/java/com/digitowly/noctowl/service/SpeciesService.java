package com.digitowly.noctowl.service;

import com.digitowly.noctowl.model.SpeciesResponse;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.enums.TaxonCheckStrategy;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.dto.FindSpeciesParams;
import com.digitowly.noctowl.service.dto.FindTaxonomyParams;
import com.digitowly.noctowl.service.scrape.AnimalScrapeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SpeciesService {

    private TaxonomyService taxonomyService;
    private AnimalScrapeService animalScrapeService;

    public SpeciesResponse findByCommonName(FindSpeciesParams params) {
        return findFirstByParams(params, TaxonCheckStrategy.WIKIDATA_ID);
    }

    public SpeciesResponse findByScientificName(FindSpeciesParams params) {
        return findFirstByParams(params, TaxonCheckStrategy.WIKIPEDIA_INFOBOX);
    }

    private SpeciesResponse findFirstByParams(FindSpeciesParams speciesParams, TaxonCheckStrategy strategy) {
        var params = getTaxonomyParams(speciesParams, 1, strategy);
        var response = taxonomyService.find(params);
        if (response == null || response.entries().isEmpty()) return null;
        var taxonomy = response.entries().getFirst();
        var animalScrape = animalScrapeService.scrape(taxonomy.wikipedia().scientificName());
        return new SpeciesResponse(taxonomy, animalScrape);
    }

    private static FindTaxonomyParams getTaxonomyParams(
            FindSpeciesParams speciesParams,
            int limit,
            TaxonCheckStrategy strategy
    ) {
        var langType = LanguageType.valueOf(speciesParams.lang().toUpperCase());
        var taxonType = getTaxonTypeFromPathVariable(speciesParams.type());
        return FindTaxonomyParams.builder()
                .type(taxonType)
                .name(speciesParams.name())
                .langType(langType)
                .entryLimit(limit)
                .strategy(strategy)
                .build();
    }

    private static TaxonType getTaxonTypeFromPathVariable(String pathVariable) {
        return switch (pathVariable) {
            case "plant", "plants" -> TaxonType.PLANT;
            case "animal", "animals" -> TaxonType.ANIMAL;
            default -> null;
        };
    }
}
