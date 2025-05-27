package com.digitowly.noctowl.service.wikidata;

import com.digitowly.noctowl.client.WikidataClient;
import com.digitowly.noctowl.model.wikidata.WikidataProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Slf4j
public class WikidataTaxonChecker {

    private static final String ANIMALIA_QID = "Q729";

    private final ObjectMapper objectMapper;
    private final WikidataClient wikidataClient;

    // In-memory caches
    private final Set<String> knownAnimals = ConcurrentHashMap.newKeySet();
    private final Set<String> knownNonAnimals = ConcurrentHashMap.newKeySet();

    public boolean isAnimal(String wikidataId) {
        if (knownAnimals.contains(wikidataId)) {
            log.info("Cache hit: {} is known to be an animal.", wikidataId);
            return true;
        }
        if (knownNonAnimals.contains(wikidataId)) {
            log.info("Cache hit: {} is known NOT to be an animal.", wikidataId);
            return false;
        }

        Set<String> visited = new LinkedHashSet<>();
        log.info("Checking if Wikidata entity {} is an animal (descendant of {}).", wikidataId, ANIMALIA_QID);
        boolean result = isTaxon(wikidataId, ANIMALIA_QID, visited);
        log.info("Result for {}: {}", wikidataId, result ? "IS an animal" : "NOT an animal");
        log.info("Visited taxon path: {}", visited);

        // Cache all visited taxa as animal or non-animal
        if (result) {
            knownAnimals.addAll(visited);
        } else {
            knownNonAnimals.addAll(visited);
        }

        return result;
    }

    private boolean isTaxon(String currentId, String targetId, Set<String> visited) {
        visited.add(currentId);

        if (knownAnimals.contains(currentId)) return true;
        if (knownNonAnimals.contains(currentId)) return false;
        if (currentId.equals(targetId)) return true;

        try {
            String json = wikidataClient.getClaims(currentId);
            if (json == null) return false;

            JsonNode claimsNode = objectMapper.readTree(json).path("claims");

            // Try "parent taxon" property (P171) first
            if (followPropertyPath(claimsNode, WikidataProperty.PARENT_TAXON, targetId, visited)) {
                return true;
            }

            // Fallback: Try "subclass of" property (P279)
            if (followPropertyPath(claimsNode, WikidataProperty.SUBCLASS_OF, targetId, visited)) {
                return true;
            }

        } catch (Exception e) {
            log.error("Error checking taxon for {}: {}", currentId, e.getMessage(), e);
        }

        return false;
    }

    private boolean followPropertyPath(
            JsonNode claimsNode,
            WikidataProperty property,
            String targetId,
            Set<String> visited)
    {
        JsonNode propertyClaims = claimsNode.path(property.getId());

        if (!propertyClaims.isArray() || propertyClaims.isEmpty()) {
            return false;
        }

        for (JsonNode claim : propertyClaims) {
            String nextId = claim.at("/mainsnak/datavalue/value/id").asText(null);
            if (nextId == null || visited.contains(nextId)) continue;

            if (isTaxon(nextId, targetId, visited)) {
                return true;
            }
        }

        return false;
    }
}
