package com.digitowly.noctowl.service.scrape;

import com.digitowly.noctowl.model.enums.ConservationStatus;
import com.digitowly.noctowl.service.scrape.dto.WikipediaInfobox;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AnimalWikipediaScrapeService {
    private static final String BASE_URL = "https://en.wikipedia.org/wiki/";

    private final ScrapeService scrapeService;

    public WikipediaInfobox scrapeInfobox(String scientificName) {
        var url = BASE_URL + scientificName.replaceAll(" ", "_");
        Document doc = scrapeService.getPage(url);
        Element infobox = doc.getElementsByClass("infobox biota").first();
        if (infobox == null) return null;

        var taxonomy = getTaxonomy(infobox);
        var commonName = getCommonName(infobox);
        var conservationStatus = getConservationStatus(infobox);

        return WikipediaInfobox.builder()
                .scientificName(scientificName)
                .commonName(commonName)
                .conservationStatus(conservationStatus)
                .taxonomy(taxonomy)
                .build();
    }

    private WikipediaInfobox.Taxonomy getTaxonomy(Element infobox) {
        var kingdom = getTaxonomyElement("Kingdom", infobox);
        var phylum = getTaxonomyElement("Phylum", infobox);
        var taxonomyClass = getTaxonomyElement("Class", infobox);
        var order = getTaxonomyElement("Order", infobox);
        var family = getTaxonomyElement("Family", infobox);
        var genus = getTaxonomyElement("Genus", infobox);

        return WikipediaInfobox.Taxonomy.builder()
                .kingdom(kingdom)
                .phylum(phylum)
                .taxonomyClass(taxonomyClass)
                .order(order)
                .family(family)
                .genus(genus)
                .build();
    }

    private WikipediaInfobox.Taxonomy.Element getTaxonomyElement(String name, Element infobox) {
        Element element = infobox.getElementsContainingOwnText(name + ":").first();
        if (element == null) return null;

        var elementSibling = element.nextElementSibling();
        if (elementSibling == null) return null;

        var elementSiblingChild = elementSibling.child(0);

        var scientificName = elementSiblingChild.text();
        var commonName = elementSiblingChild.attr("title");

        return WikipediaInfobox.Taxonomy.Element.builder()
                .commonName(commonName)
                .scientificName(scientificName)
                .build();
    }

    private String getCommonName(Element infobox) {
        var tableHeader = infobox.getElementsByTag("th").first();
        if (tableHeader == null) return null;
        return tableHeader.text();
    }

    private ConservationStatus getConservationStatus(Element infobox) {
        var tableHeaders = infobox.getElementsByTag("th");
        var hasConservationStatus = tableHeaders.stream()
                .anyMatch(th -> th.text().equals("Conservation status"));
        if (!hasConservationStatus) return null;

        var tableRows = infobox.getElementsByTag("tr");
        var linksInTableRows = tableRows.stream()
                .flatMap(tr -> tr.getElementsByTag("a").stream());

        return linksInTableRows
                .map(a -> findConservationStatusByName(a.text().trim()))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private ConservationStatus findConservationStatusByName(String name) {
        return Arrays.stream(ConservationStatus.values())
                .filter(c -> c.getName().equals(name))
                .findFirst().orElse(null);
    }
}
