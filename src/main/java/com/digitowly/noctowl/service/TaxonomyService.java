package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.entity.TaxonomyEntryEntity;
import com.digitowly.noctowl.model.wikidata.WikimediaPageDto;
import com.digitowly.noctowl.model.dto.TaxonomyResponse;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.repository.TaxonomyEntryRepository;
import com.digitowly.noctowl.service.wikidata.WikidataTaxonChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final TaxonomyEntryRepository repository;
    private final ObjectMapper objectMapper;

    public TaxonomyResponse find(TaxonType type, String name) {
        var id =  type + "-" + name;
        var cachedResult = getStoredResponse(id);
        if (cachedResult != null) return cachedResult;

        var pagesResponse = wikimediaClient.getPages(name);
        log.info("Searching wiki pages...");
        for (WikimediaPageDto page : pagesResponse.pages()) {
            var summary = wikipediaClient.getSummary(page.key());
            var response = findByTaxonType(type, summary.wikibase_item(), page);
            if (response == null) continue;
            storeResponse(id, response);
            return response;
        }
        return null;
    }

    private TaxonomyResponse findByTaxonType(TaxonType type, String wikibaseId, WikimediaPageDto page) {
        var isTaxon = wikidataTaxonChecker.isTaxon(type, wikibaseId);
        if (!isTaxon) return null;
        return new TaxonomyResponse(type, getWikipediaInfo(page));
    }

    private TaxonomyResponse.WikipediaInfo getWikipediaInfo(WikimediaPageDto page) {
        return new TaxonomyResponse.WikipediaInfo(
                page.id(),
                page.title(),
                page.key(),
                page.description()
        );
    }

    private TaxonomyResponse getStoredResponse(String id)  {
        var cachedResult = repository.findById(id).orElse(null);
        if (cachedResult == null) return null;
        try {
            return objectMapper.readValue(cachedResult.getResponse(), TaxonomyResponse.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private void storeResponse(String id, TaxonomyResponse response) {
        try {
            var taxonomyEntry = TaxonomyEntryEntity.builder()
                    .id(id)
                    .response(objectMapper.writeValueAsString(response))
                    .build();

            repository.save(taxonomyEntry);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
