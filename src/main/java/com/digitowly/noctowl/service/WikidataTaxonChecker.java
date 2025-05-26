package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikidataClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class WikidataTaxonChecker {

    private static final String PARENT_TAXON_ID = "P171";
    private static final String ANIMALIA_QID = "Q729";

    private final ObjectMapper objectMapper;
    private final WikidataClient wikidataClient;

    public boolean isAnimal(String wikidataId) {
        Set<String> visited = new LinkedHashSet<>();
        log.info("Checking if Wikidata entity {} is an animal (descendant of {}).", wikidataId, ANIMALIA_QID);
        boolean result = isTaxon(wikidataId, ANIMALIA_QID, visited);
        log.info("Result for {}: {}", wikidataId, result ? "IS an animal" : "NOT an animal");
        log.info("Visited taxon path: {}", visited);
        return result;
    }

    private boolean isTaxon(String currentId, String targetId, Set<String> visited) {
        visited.add(currentId);
        if (currentId.equals(targetId)) {
            log.debug("Matched target taxon: {}", targetId);
            return true;
        }

        try {
            String json = wikidataClient.getEntity(currentId);
            if (json == null) {
                log.warn("Received null JSON from Wikidata for ID {}", currentId);
                return false;
            }

            JsonNode root = objectMapper.readTree(json);
            JsonNode claims = root.at("/entities/" + currentId + "/claims/" + PARENT_TAXON_ID);

            if (claims == null || !claims.isArray() || claims.isEmpty()) {
                log.warn("No '{}' claims found for {}", PARENT_TAXON_ID, currentId);
                return false;
            }

            for (JsonNode claim : claims) {
                JsonNode nextIdNode = claim.at("/mainsnak/datavalue/value/id");
                if (!nextIdNode.isTextual()) {
                    log.debug("Skipped claim with missing or non-textual ID for {}", currentId);
                    continue;
                }

                String nextId = nextIdNode.asText();
                log.debug("Found parent taxon {} for {}", nextId, currentId);
                if (visited.contains(nextId)) {
                    log.debug("Already visited {}", nextId);
                    continue;
                }

                if (isTaxon(nextId, targetId, visited)) return true;
            }

        } catch (Exception e) {
            log.error("Error while checking taxon hierarchy for {}: {}", currentId, e.getMessage(), e);
        }

        log.debug("Entity {} is not a descendant of {}", currentId, targetId);
        return false;
    }
}
