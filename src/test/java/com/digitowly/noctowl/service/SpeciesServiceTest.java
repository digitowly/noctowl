package com.digitowly.noctowl.service;

import com.digitowly.noctowl.model.SpeciesResponse;
import com.digitowly.noctowl.model.dto.TaxonomyEntry;
import com.digitowly.noctowl.model.dto.TaxonomyResponse;
import com.digitowly.noctowl.model.enums.ConservationStatus;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.enums.TaxonCheckStrategy;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.dto.FindSpeciesParams;
import com.digitowly.noctowl.service.dto.FindTaxonomyParams;
import com.digitowly.noctowl.service.scrape.AnimalScrapeService;
import com.digitowly.noctowl.service.wikipedia.dto.AnimalScrapeResult;
import com.digitowly.noctowl.service.wikipedia.dto.WikipediaInfobox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpeciesServiceTest {

    private TaxonomyService taxonomyService;
    private AnimalScrapeService animalScrapeService;

    private SpeciesService speciesService;

    @BeforeEach
    void setUp() {
        this.taxonomyService = mock(TaxonomyService.class);
        this.animalScrapeService = mock(AnimalScrapeService.class);
        this.speciesService = new SpeciesService(taxonomyService, animalScrapeService);
    }

    @Test
    void findByCommonName() {
        var taxonomyFindParams = FindTaxonomyParams.builder()
                .strategy(TaxonCheckStrategy.WIKIDATA_ID)
                .name("tawny owl")
                .langType(LanguageType.EN)
                .entryLimit(1)
                .type(TaxonType.ANIMAL)
                .build();
        when(taxonomyService.find(taxonomyFindParams)).thenReturn(mockTaxonomyEntry());

        when(animalScrapeService.scrape("Strix aluco")).thenReturn(mockAnimalScrapeResult());

        var params = new FindSpeciesParams("en", "animal", "tawny owl");
        var result = speciesService.findByCommonName(params);
        assertEquals("Strix aluco", result.getBinomialName());
        assertEquals(ConservationStatus.LeastConcern.getName(), result.getConservationStatus());
    }

    @Test
    void findByScientificName() {
        var taxonomyFindParams = FindTaxonomyParams.builder()
                .strategy(TaxonCheckStrategy.WIKIPEDIA_INFOBOX)
                .name("Strix aluco")
                .langType(LanguageType.EN)
                .entryLimit(1)
                .type(TaxonType.ANIMAL)
                .build();
        when(taxonomyService.find(taxonomyFindParams)).thenReturn(mockTaxonomyEntry());

        when(animalScrapeService.scrape("Strix aluco")).thenReturn(mockAnimalScrapeResult());

        var params = new FindSpeciesParams("en", "animal", "Strix aluco");
        var result = speciesService.findByScientificName(params);
        assertEquals("Strix aluco", result.getBinomialName());
        assertEquals(ConservationStatus.LeastConcern.getName(), result.getConservationStatus());
    }

    private static TaxonomyResponse mockTaxonomyEntry() {
        var entry = new TaxonomyEntry(
                TaxonType.ANIMAL,
                TaxonomyEntry.WikipediaInfo.builder()
                        .scientificName("Strix aluco")
                        .title("Strix aluco")
                        .build(),
                TaxonomyEntry.GbifInfo.builder()
                        .canonicalName("Strix aluco")
                        .taxonKey("12345")
                        .build()
        );
        return TaxonomyResponse.builder()
                .entries(List.of(entry))
                .type(TaxonType.ANIMAL)
                .build();
    }

    private static AnimalScrapeResult mockAnimalScrapeResult() {
        return new AnimalScrapeResult(
                WikipediaInfobox.builder()
                        .scientificName("Strix aluco")
                        .conservationStatus(Optional.of(ConservationStatus.LeastConcern))
                        .taxonomy(
                                Optional.ofNullable(WikipediaInfobox.Taxonomy.builder().build())
                        )
                        .build()
        );
    }
}