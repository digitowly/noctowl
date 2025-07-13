package com.digitowly.noctowl.model.dto;

import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.enums.TaxonType;
import lombok.Builder;

import java.util.Map;

public record TaxonomyEntry(
        TaxonType type,
        WikipediaInfo wikipedia,
        GbifInfo gbif
) {
    @Builder
    public record WikipediaInfo(
            Integer id,
            String title,
            String key,
            String scientificName,
            String description,
            Map<LanguageType, String> lang
    ) {
    }

    @Builder
    public record GbifInfo(
            String taxonKey,
            String name,
            String canonicalName
    ) {
    }
}