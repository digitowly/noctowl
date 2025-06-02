package com.digitowly.noctowl.service.wikidata;

import com.digitowly.noctowl.client.WikidataClient;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.model.enums.wikidata.WikidataProperty;
import com.digitowly.noctowl.repository.TaxonomyTreeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class WikidataTaxonChecker {

    private static final String ANIMALIA_QID = "Q729";

    private final ObjectMapper objectMapper;
    private final WikidataClient wikidataClient;
    private final TaxonomyTreeRepository taxonomyTreeRepository;

    public boolean isTaxon(TaxonType taxonType, String wikidataId) {
        if (isCacheHit(taxonType, wikidataId)) return true;

        Set<String> visitedTaxonIds = new LinkedHashSet<>();
        log.info("Checking if Wikidata entity {} is an animal (descendant of {}).", wikidataId, ANIMALIA_QID);
        boolean result = isWikidataTaxon(taxonType, wikidataId, ANIMALIA_QID, visitedTaxonIds);
        log.info("Result for {}: {}", wikidataId, result ? "IS an animal" : "NOT an animal");
        log.info("Visited taxon path: {}", visitedTaxonIds);

        if (!result) return false;

        // Cache all visited taxa as animal or non-animal
        switch (taxonType) {
            case ANIMAL -> taxonomyTreeRepository.addWikiAnimalIds(visitedTaxonIds);
        }
        return true;
    }

    private boolean isWikidataTaxon(
            TaxonType taxonType,
            String currentId,
            String targetId,
            @NotNull Set<String> visitedIds
    ) {
        visitedIds.add(currentId);

        if (isCacheHit(taxonType, currentId)) return true;
        if (currentId.equals(targetId)) return true;

        try {
            String json = wikidataClient.getClaims(currentId);
            if (json == null) return false;

            JsonNode claimsNode = objectMapper.readTree(json).path("claims");

            // Try "parent taxon" property (P171) first
            if (followPropertyPath(taxonType, claimsNode, WikidataProperty.PARENT_TAXON, targetId, visitedIds)) {
                return true;
            }

            // Fallback: Try "subclass of" property (P279)
            if (followPropertyPath(taxonType, claimsNode, WikidataProperty.SUBCLASS_OF, targetId, visitedIds)) {
                return true;
            }

        } catch (Exception e) {
            log.error("Error checking taxon for {}: {}", currentId, e.getMessage(), e);
        }

        return false;
    }

    private boolean followPropertyPath(
            TaxonType taxonType,
            @NotNull JsonNode claimsNode,
            @NotNull WikidataProperty property,
            String targetId,
            Set<String> visitedIds
    ) {
        JsonNode propertyClaims = claimsNode.path(property.getId());

        if (!propertyClaims.isArray() || propertyClaims.isEmpty()) {
            return false;
        }

        for (JsonNode claim : propertyClaims) {
            String nextId = claim.at("/mainsnak/datavalue/value/id").asText(null);
            if (nextId == null || visitedIds.contains(nextId)) continue;

            if (isWikidataTaxon(taxonType, nextId, targetId, visitedIds)) {
                return true;
            }
        }

        return false;
    }

    private boolean isCacheHit(@NotNull TaxonType taxonType, String wikidataId) {
        boolean isCached = switch (taxonType) {
            case ANIMAL -> taxonomyTreeRepository.hasWikiAnimalId(wikidataId);
            default -> false;
        };
        if (isCached) log.info("Cache hit: {} is a known {}.", wikidataId, taxonType);
        return isCached;
    }
}
