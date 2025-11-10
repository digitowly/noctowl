package com.digitowly.noctowl.model;

import com.digitowly.noctowl.model.dto.TaxonomyEntry;
import com.digitowly.noctowl.model.enums.ConservationStatus;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.service.wikipedia.dto.AnimalScrapeResult;
import com.digitowly.noctowl.service.wikipedia.dto.WikipediaInfobox;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        var wikipediaInfobox = Optional.ofNullable(animalScrapeResult)
                .map(AnimalScrapeResult::wikipediaInfobox)
                .orElse(null);
        var animalTaxonomy = Optional.ofNullable(wikipediaInfobox)
                .flatMap(WikipediaInfobox::taxonomy);

        var wikipediaInfo = Optional.ofNullable(taxonomyEntry)
                .map(TaxonomyEntry::wikipedia)
                .orElse(null);

        var gbifInfo = Optional.ofNullable(taxonomyEntry)
                .map(TaxonomyEntry::gbif)
                .orElse(null);

        name = Optional.ofNullable(wikipediaInfo)
                .map(TaxonomyEntry.WikipediaInfo::title)
                .orElse(null);

        binomialName = Optional.ofNullable(wikipediaInfo)
                .map(TaxonomyEntry.WikipediaInfo::scientificName)
                .orElse(null);

        taxonomyClass = animalTaxonomy
                .map(WikipediaInfobox.Taxonomy::taxonomyClass)
                .orElse(Optional.empty())
                .map(WikipediaInfobox.Taxonomy.Element::scientificName)
                .orElse(null);

        order = animalTaxonomy
                .map(WikipediaInfobox.Taxonomy::order)
                .orElse(Optional.empty())
                .map(WikipediaInfobox.Taxonomy.Element::scientificName)
                .orElse(null);

        family = animalTaxonomy
                .map(WikipediaInfobox.Taxonomy::family)
                .orElse(Optional.empty())
                .map(WikipediaInfobox.Taxonomy.Element::scientificName)
                .orElse(null);

        genus = animalTaxonomy
                .map(WikipediaInfobox.Taxonomy::genus)
                .orElse(Optional.empty())
                .map(WikipediaInfobox.Taxonomy.Element::scientificName)
                .orElse(null);

        gbifKey = Optional.ofNullable(gbifInfo)
                .map(TaxonomyEntry.GbifInfo::taxonKey)
                .orElse(null);

        translatedNames = mapTranslatedNames(Optional.ofNullable(wikipediaInfo)
                .map(TaxonomyEntry.WikipediaInfo::lang)
                .orElse(null));

        conservationStatus = Optional.ofNullable(wikipediaInfobox)
                .flatMap(WikipediaInfobox::conservationStatus)
                .map(ConservationStatus::getName)
                .orElse(null);
    }


    private static Map<String, String> mapTranslatedNames(Map<LanguageType, String> translatedNames) {
        var result = new HashMap<String, String>();
        if (translatedNames == null || translatedNames.isEmpty()) return result;
        translatedNames.forEach((languageType, name) -> {
            if (name == null || name.isBlank() || languageType == null) return;
            result.put(languageType.getName(), name);
        });
        return result;
    }
}
