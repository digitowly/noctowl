package com.digitowly.noctowl.service.wikidata;

import com.digitowly.noctowl.client.WikidataClient;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.model.enums.wikidata.WikidataProperty;
import com.digitowly.noctowl.service.storage.TaxonomyTreeStorageHandler;
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

    private final ObjectMapper objectMapper;
    private final WikidataClient wikidataClient;
    private final TaxonomyTreeStorageHandler storageHandler;

    public boolean isTaxon(TaxonType type, String wikidataId) {
        if (storageHandler.isTaxonCached(type, wikidataId)) return true;

        var root = type.getWikidataQID();
        if (root == null) return false;

        Set<String> visitedTaxonIds = new LinkedHashSet<>();
        log.info("Checking if Wikidata entity {} is of type {} (descendant of {}).", wikidataId, type, root);
        boolean result = isWikidataTaxon(type, wikidataId, root.getId(), visitedTaxonIds);
        log.info("Result for {}: {}", wikidataId, result ? "IS requested taxon" : "NOT requested taxon");
        log.info("Visited taxon path: {}", visitedTaxonIds);

        if (!result) return false;

        storageHandler.storeChildren(root, visitedTaxonIds);
        return true;
    }

    private boolean isWikidataTaxon(
            TaxonType taxonType,
            String currentId,
            String targetId,
            @NotNull Set<String> visitedIds
    ) {
        visitedIds.add(currentId);

        if (storageHandler.isTaxonCached(taxonType, currentId)) return true;
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

            // if there is no next id, or it is already visited -> skip
            if (nextId == null || visitedIds.contains(nextId)) continue;

            if (isWikidataTaxon(taxonType, nextId, targetId, visitedIds)) {
                return true;
            }
        }

        return false;
    }
}
