package com.digitowly.noctowl.model.dto;

import com.digitowly.noctowl.model.enums.TaxonType;
import lombok.Builder;

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
            String description
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