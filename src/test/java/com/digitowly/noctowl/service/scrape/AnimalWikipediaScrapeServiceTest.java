package com.digitowly.noctowl.service.scrape;

import com.digitowly.noctowl.model.enums.ConservationStatus;
import com.digitowly.noctowl.service.wikipedia.dto.WikipediaInfobox;
import com.digitowly.noctowl.service.wikipedia.AnimalWikipediaScrapeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimalWikipediaScrapeServiceTest {

    private ScrapeService scrapeService;
    private AnimalWikipediaScrapeService animalWikipediaScrapeService;

    @BeforeEach
    void setUp() {
        this.scrapeService = mock(ScrapeService.class);
        this.animalWikipediaScrapeService = new AnimalWikipediaScrapeService(this.scrapeService);
    }

    @Test
    void scrapeInfobox() {
        var scientificName = "Strix aluco";
        var expectedInfobox = WikipediaInfobox.builder()
                .scientificName(scientificName)
                .commonName("Tawny owl")
                .conservationStatus(Optional.of(ConservationStatus.LeastConcern))
                .taxonomy(Optional.ofNullable(WikipediaInfobox.Taxonomy.builder()
                        .kingdom(Optional.of(new WikipediaInfobox.Taxonomy.Element(
                                "Animalia", "Animal"
                        )))
                        .phylum(Optional.of(new WikipediaInfobox.Taxonomy.Element(
                                "Chordata", "Chordate"
                        )))
                        .build()))
                .build();

        Document mockDoc = Jsoup.parse("""
                    <html>
                      <body>
                        <table class="infobox biota">
                          <tr><th>Tawny owl</th></tr>
                          <tr><th>Conservation status</th></tr>
                          <tr><td><a href="/wiki/Least_Concern" title="Least Concern">Least Concern</a></td></tr>
                          <tr><td>Kingdom:</td><td><a href="/wiki/Animal" title="Animal">Animalia</a></td></tr>
                          <tr><td>Phylum:</td><td><a href="/wiki/Chordate" title="Chordate">Chordata</a></td></tr>
                        </table>
                      </body>
                    </html>
                """);

        when(scrapeService.getPage("https://en.wikipedia.org/wiki/Strix_aluco"))
                .thenReturn(mockDoc);

        var infobox = animalWikipediaScrapeService.scrapeInfobox(scientificName);
        assertEquals(expectedInfobox, infobox);
    }

    @Test
    void scrapeInfobox_without_conservation_status() {
        var scientificName = "Strix aluco";
        var expectedInfobox = WikipediaInfobox.builder()
                .scientificName(scientificName)
                .commonName("Tawny owl")
                .conservationStatus(null)
                .taxonomy(Optional.ofNullable(WikipediaInfobox.Taxonomy.builder()
                        .kingdom(Optional.of(new WikipediaInfobox.Taxonomy.Element(
                                "Animalia", "Animal"
                        )))
                        .phylum(Optional.of(new WikipediaInfobox.Taxonomy.Element(
                                "Chordata", "Chordate"
                        )))
                        .build()))
                .build();

        Document mockDoc = Jsoup.parse("""
                    <html>
                      <body>
                        <table class="infobox biota">
                          <tr><th>Tawny owl</th></tr>
                          <tr><td><a href="/wiki/Least_Concern" title="Least Concern">Least Concern</a></td></tr>
                          <tr><td>Kingdom:</td><td><a href="/wiki/Animal" title="Animal">Animalia</a></td></tr>
                          <tr><td>Phylum:</td><td><a href="/wiki/Chordate" title="Chordate">Chordata</a></td></tr>
                        </table>
                      </body>
                    </html>
                """);

        when(scrapeService.getPage("https://en.wikipedia.org/wiki/Strix_aluco"))
                .thenReturn(mockDoc);

        var infobox = animalWikipediaScrapeService.scrapeInfobox(scientificName);
        assertEquals(expectedInfobox, infobox);
    }

    @Test
    void scrapeInfobox_no_infobox() {
        var scientificName = "Strix aluco";
        Document mockDoc = Jsoup.parse("""
                    <html>
                      <body>
                        <table class="">
                          <tr><th>Invalid box</th></tr>
                        </table>
                      </body>
                    </html>
                """);

        when(scrapeService.getPage("https://en.wikipedia.org/wiki/Strix_aluco"))
                .thenReturn(mockDoc);

        var infobox = animalWikipediaScrapeService.scrapeInfobox(scientificName);
        assertNull(infobox);
    }
}