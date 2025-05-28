package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.client.dto.WikimediaPageDto;
import com.digitowly.noctowl.client.dto.WikimediaPagesDto;
import com.digitowly.noctowl.client.dto.WikipediaSummaryDto;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.wikidata.WikidataTaxonChecker;
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
        this.taxonomyService = new TaxonomyService(wikipediaClient, wikimediaClient, wikidataTaxonChecker);
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
        when(wikidataTaxonChecker.isAnimal(wikidataId)).thenReturn(true);

        var result = taxonomyService.find(TaxonType.ANIMAL, name);
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(TaxonType.ANIMAL);
        assertThat(result.wikipediaInfo().title()).isEqualTo(name);
        assertThat(result.wikipediaInfo().key()).isEqualTo(key);
    }
}