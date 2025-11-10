package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.GbifClient;
import com.digitowly.noctowl.client.WikimediaClient;
import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.dto.TaxonomyEntry;
import com.digitowly.noctowl.model.dto.gbif.GbifSpeciesResponseDto;
import com.digitowly.noctowl.model.entity.TaxonomyEntryEntity;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.wikidata.WikimediaPageDto;
import com.digitowly.noctowl.model.dto.TaxonomyResponse;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.repository.TaxonomyEntryRepository;
import com.digitowly.noctowl.service.dto.FindTaxonomyParams;
import com.digitowly.noctowl.service.wikidata.WikidataTaxonChecker;
import com.digitowly.noctowl.service.wikipedia.WikipediaTaxonChecker;
import com.digitowly.noctowl.util.GbifSpeciesValidator;
import com.digitowly.noctowl.util.LatinNameExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TaxonomyService {

    private final WikipediaClient wikipediaClient;
    private final WikimediaClient wikimediaClient;
    private final GbifClient gbifClient;

    private final TranslationService translationService;
    private final WikidataTaxonChecker wikidataTaxonChecker;
    private final WikipediaTaxonChecker wikipediaTaxonChecker;

    private final TaxonomyEntryRepository repository;
    private final ObjectMapper objectMapper;

    public TaxonomyResponse find(FindTaxonomyParams params) {
        var id = createId(params);
        var cachedResult = getStoredResponse(id);
        if (cachedResult != null) return cachedResult;

        var pagesResponse = wikimediaClient.getPages(params.name(), params.langType());
        log.info("Searching wiki pages...");
        List<TaxonomyEntry> entries = new ArrayList<>();
        var limit = params.entryLimit() != null ? params.entryLimit() : 1;
        for (WikimediaPageDto page : pagesResponse.pages()) {
            if (entries.size() >= limit) break;
            var entry = switch (params.strategy()) {
                case WIKIDATA_ID -> findEntryByWikibaseId(params.langType(), params.type(), page);
                case WIKIPEDIA_INFOBOX -> findEntryByWikipediaInfobox(params.type(), params.name(), page);
            };
            if (entry == null) continue;
            entries.add(entry);
        }
        if (entries.isEmpty()) return null;
        var response = new TaxonomyResponse(params.type(), entries);
        storeResponse(id, response);
        return response;
    }

    private TaxonomyEntry findEntryByWikipediaInfobox(
            TaxonType type,
            String scientificName,
            WikimediaPageDto page
    ) {
        var isTaxon = wikipediaTaxonChecker.isTaxon(type, scientificName);
        if (!isTaxon) return null;
        return getTaxonomyEntry(type, scientificName, page);
    }

    private TaxonomyEntry findEntryByWikibaseId(
            LanguageType langType,
            TaxonType type,
            WikimediaPageDto page
    ) {
        var summary = wikipediaClient.getSummary(page.key(), langType);
        if (summary == null) return null;

        var isTaxon = wikidataTaxonChecker.isTaxon(type, summary.wikibase_item());
        if (!isTaxon) return null;

        var potentialLatinName = LatinNameExtractor.extract(page.excerpt());
        return getTaxonomyEntry(type, potentialLatinName, page);
    }

    private TaxonomyEntry getTaxonomyEntry(
            TaxonType type,
            String scientificName,
            WikimediaPageDto page
    ) {
        var gbifResult = gbifClient.getSpecies(scientificName);

        var isValidResult = GbifSpeciesValidator.validate(gbifResult);
        if (!isValidResult) return null;

        var gbifInfo = getGbifInfo(gbifResult);
        var wikipediaInfo = getWikipediaInfo(page, gbifInfo.canonicalName());
        return new TaxonomyEntry(type, wikipediaInfo, gbifInfo);
    }

    private TaxonomyEntry.GbifInfo getGbifInfo(
            GbifSpeciesResponseDto gbifSpeciesResponseDto
    ) {
        return TaxonomyEntry.GbifInfo.builder()
                .taxonKey(gbifSpeciesResponseDto.usage().key())
                .name(gbifSpeciesResponseDto.usage().name())
                .canonicalName(gbifSpeciesResponseDto.usage().canonicalName())
                .build();
    }

    private TaxonomyEntry.WikipediaInfo getWikipediaInfo(
            WikimediaPageDto page,
            String scientificName
    ) {
        var translations = translationService.getTranslations(scientificName);
        return TaxonomyEntry.WikipediaInfo.builder()
                .id(page.id())
                .title(page.title())
                .key(page.key())
                .scientificName(scientificName)
                .description(page.description())
                .lang(translations)
                .build();
    }

    private TaxonomyResponse getStoredResponse(String id) {
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

    private String createId(FindTaxonomyParams params) {
        var name = params.name().toLowerCase().trim().replaceAll(" ", "-");
        if (params.entryLimit() == null) return params.type() + "-" + name;
        return params.type() + "-"
                + name + "-"
                + params.langType().getName() + "-"
                + params.entryLimit();
    }

}
