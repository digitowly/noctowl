package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.wikidata.WikimediaPageDto;
import com.digitowly.noctowl.model.dto.TaxonomyResponse;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.wikidata.WikidataTaxonChecker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TaxonomyService {

    private final WikipediaClient wikipediaClient;
    private final WikimediaClient wikimediaClient;
    private final WikidataTaxonChecker wikidataTaxonChecker;

    public TaxonomyResponse find(TaxonType type, String name) {
        var pagesResponse = wikimediaClient.getPages(name);

        log.info("Searching wiki pages...");
        for (WikimediaPageDto page : pagesResponse.pages()) {
            var summary = wikipediaClient.getSummary(page.key());
            var response = switch (type) {
                case ANIMAL -> findAnimal(summary.wikibase_item(), page);
                default -> null;
            };
            if (response == null) continue;
            return response;
        }
        return null;
    }

    private TaxonomyResponse findAnimal(String wikibaseId, WikimediaPageDto page) {
        var isAnimal = wikidataTaxonChecker.isTaxon(TaxonType.ANIMAL, wikibaseId);
        if (!isAnimal) { return null; }
        return new TaxonomyResponse(TaxonType.ANIMAL, getWikipediaInfo(page));
    }

    private TaxonomyResponse.WikipediaInfo getWikipediaInfo(WikimediaPageDto page) {
        return new TaxonomyResponse.WikipediaInfo(
                page.id(),
                page.title(),
                page.key(),
                page.description()
        );
    };

}
