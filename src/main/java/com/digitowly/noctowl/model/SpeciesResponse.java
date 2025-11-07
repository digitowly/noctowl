package com.digitowly.noctowl.model;

import com.digitowly.noctowl.model.dto.TaxonomyEntry;
import com.digitowly.noctowl.model.enums.ConservationStatus;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.service.scrape.dto.AnimalScrapeResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class SpeciesResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("binomial_name")
    private String binomialName;

    @JsonProperty("class")
    private String taxonomyClass;

    @JsonProperty("order")
    private String order;

    @JsonProperty("family")
    private String family;

    @JsonProperty("genus")
    private String genus;

    @JsonProperty("species")
    private String species;

    @JsonProperty("gbif_key")
    private String gbifKey;

    @JsonProperty("translated_names")
    private Map<String, String> translatedNames;

    @JsonProperty("conservation_status")
    private String conservationStatus;

    public SpeciesResponse(
            TaxonomyEntry taxonomyEntry,
            AnimalScrapeResult animalScrapeResult
    ) {
        var wikipediaInfobox = animalScrapeResult.wikipediaInfobox();
        var animalTaxonomy = wikipediaInfobox.taxonomy();

        name = taxonomyEntry.wikipedia().title();
        binomialName = taxonomyEntry.wikipedia().scientificName();
        taxonomyClass = animalTaxonomy.taxonomyClass().scientificName();
        order = animalTaxonomy.order().scientificName();
        family = animalTaxonomy.family().scientificName();
        genus = animalTaxonomy.genus().scientificName();
        gbifKey = taxonomyEntry.gbif().taxonKey();
        translatedNames = mapTranslatedNames(taxonomyEntry.wikipedia().lang());
        conservationStatus = formatConservationStatus(wikipediaInfobox.conservationStatus());
    }

    private static Map<String, String> mapTranslatedNames(Map<LanguageType, String> translatedNames) {
        var result = new HashMap<String, String>();
        translatedNames.forEach((languageType, name) -> {
            if (name == null || name.isBlank()) return;
            result.put(languageType.getName(), name);
        });
        return result;
    }

    private static String formatConservationStatus(ConservationStatus conservationStatus) {
        return conservationStatus.getName() + " " + "(" + conservationStatus.getCode() + ")";
    }
}
