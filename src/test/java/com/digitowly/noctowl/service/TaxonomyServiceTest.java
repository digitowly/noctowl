package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.GbifClient;
import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.dto.gbif.GbifSpeciesResponseDto;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.wikidata.WikimediaPageDto;
import com.digitowly.noctowl.model.wikidata.WikimediaPagesDto;
import com.digitowly.noctowl.model.wikidata.WikipediaSummaryDto;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.repository.TaxonomyEntryRepository;
import com.digitowly.noctowl.service.wikidata.WikidataTaxonChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaxonomyServiceTest {

    private TaxonomyService taxonomyService;
    private WikipediaClient wikipediaClient;
    private WikimediaClient wikimediaClient;
    private GbifClient gbifClient;

    private TranslationService translationService;
    private WikidataTaxonChecker wikidataTaxonChecker;

    @BeforeEach
    void setUp() {
        this.wikipediaClient = mock(WikipediaClient.class);
        this.wikimediaClient = mock(WikimediaClient.class);
        this.gbifClient = mock(GbifClient.class);

        this.translationService = mock(TranslationService.class);
        this.wikidataTaxonChecker = mock(WikidataTaxonChecker.class);
        var taxonomyEntryRepository = mock(TaxonomyEntryRepository.class);

        this.taxonomyService = new TaxonomyService(
                wikipediaClient,
                wikimediaClient,
                gbifClient,
                translationService,
                wikidataTaxonChecker,
                taxonomyEntryRepository,
                new ObjectMapper()
        );
    }

    @Test
    void find_animal_success() {
        String name = "Cat";
        String key = "Cat";
        String wikidataId = "Q146";
        var pageDto = new WikimediaPageDto(
                1234,
                key,
                name,
                "Cat (Felis catus)",
                "A cat"
        );
        var pagesDto = new WikimediaPagesDto(List.of(pageDto));
        var summaryDto = new WikipediaSummaryDto(name, wikidataId);
        var speciesResponseDto = new GbifSpeciesResponseDto(
                new GbifSpeciesResponseDto.Usage(
                        "7689",
                        "Felis catus 1",
                        "Felis catus"
                ),
                new GbifSpeciesResponseDto.Diagnostics(
                        "EXACT",
                        99
                )
        );
        when(wikimediaClient.getPages(name, LanguageType.EN)).thenReturn(pagesDto);
        when(wikipediaClient.getSummary("Cat", LanguageType.EN)).thenReturn(summaryDto);
        when(wikidataTaxonChecker.isTaxon(TaxonType.ANIMAL, wikidataId)).thenReturn(true);
        when(gbifClient.getSpecies("Felis catus")).thenReturn(speciesResponseDto);

        var result = taxonomyService.find(TaxonType.ANIMAL, name, null, LanguageType.EN);
        assertThat(result).isNotNull();
        assertEquals(TaxonType.ANIMAL, result.type());

        // wikipedia
        assertEquals(name, result.entries().getFirst().wikipedia().title());
        assertEquals(key, result.entries().getFirst().wikipedia().key());

        // gbif
        assertEquals("7689", result.entries().getFirst().gbif().taxonKey());
    }
}