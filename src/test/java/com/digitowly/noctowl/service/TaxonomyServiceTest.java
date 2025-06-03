package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaxonomyServiceTest {

    private TaxonomyService taxonomyService;
    private WikipediaClient wikipediaClient;
    private WikimediaClient wikimediaClient;
    private WikidataTaxonChecker wikidataTaxonChecker;

    @BeforeEach
    void setUp() {
        this.wikipediaClient = mock(WikipediaClient.class);
        this.wikimediaClient = mock(WikimediaClient.class);
        this.wikidataTaxonChecker = mock(WikidataTaxonChecker.class);
        var taxonomyEntryRepository = mock(TaxonomyEntryRepository.class);

        this.taxonomyService = new TaxonomyService(
                wikipediaClient,
                wikimediaClient,
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
        var pageDto = new WikimediaPageDto(1234, key, name, "A cat");
        var pagesDto = new WikimediaPagesDto(List.of(pageDto));
        var summaryDto = new WikipediaSummaryDto(name, wikidataId);
        when(wikimediaClient.getPages(name)).thenReturn(pagesDto);
        when(wikipediaClient.getSummary("Cat")).thenReturn(summaryDto);
        when(wikidataTaxonChecker.isTaxon(TaxonType.ANIMAL, wikidataId)).thenReturn(true);

        var result = taxonomyService.find(TaxonType.ANIMAL, name, null);
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(TaxonType.ANIMAL);
        assertThat(result.entries().get(0).wikipediaInfo().title()).isEqualTo(name);
        assertThat(result.entries().get(0).wikipediaInfo().key()).isEqualTo(key);
    }
}