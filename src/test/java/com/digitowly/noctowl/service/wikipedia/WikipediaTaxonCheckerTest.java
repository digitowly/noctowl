package com.digitowly.noctowl.service.wikipedia;

import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.wikipedia.dto.WikipediaInfobox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WikipediaTaxonCheckerTest {

    private AnimalWikipediaScrapeService animalWikipediaScrapeService;
    private WikipediaTaxonChecker wikipediaTaxonChecker;

    @BeforeEach
    void setUp() {
        this.animalWikipediaScrapeService = mock(AnimalWikipediaScrapeService.class);
        this.wikipediaTaxonChecker = new WikipediaTaxonChecker(animalWikipediaScrapeService);
    }

    @Test
    void isTaxon_animal_true() {
        var infobox = WikipediaInfobox.builder()
                .taxonomy(
                        Optional.ofNullable(WikipediaInfobox.Taxonomy.builder()
                                .kingdom(
                                        Optional.of(new WikipediaInfobox.Taxonomy.Element("Animalia", "Animal")))
                                .build())
                )
                .build();
        when(animalWikipediaScrapeService.scrapeInfobox("Strix aluco")).thenReturn(infobox);

        var isTaxon = wikipediaTaxonChecker.isTaxon(TaxonType.ANIMAL, "Strix aluco");
        assertTrue(isTaxon);
    }

    @Test
    void isTaxon_animal_false() {
        var infobox = WikipediaInfobox.builder()
                .taxonomy(
                        Optional.ofNullable(WikipediaInfobox.Taxonomy.builder()
                                .kingdom(Optional.of(new WikipediaInfobox.Taxonomy.Element("Plantae", "Plant")))
                                .build())
                )
                .build();
        when(animalWikipediaScrapeService.scrapeInfobox("Arecaceae")).thenReturn(infobox);

        var isTaxon = wikipediaTaxonChecker.isTaxon(TaxonType.ANIMAL, "Arecaceae");
        assertFalse(isTaxon);
    }

    @Test
    void isTaxon_plant_false() {
        // false since it is not supported yet
        var isTaxon = wikipediaTaxonChecker.isTaxon(TaxonType.PLANT, "Arecaceae");
        assertFalse(isTaxon);
    }
}