package com.digitowly.noctowl.model.dto;

import com.digitowly.noctowl.model.enums.TaxonType;
import lombok.Builder;

public record TaxonomyEntry(
        TaxonType type,
        WikipediaInfo wikipediaInfo
) {
    @Builder
    public record WikipediaInfo(
            Integer id,
            String title,
            String key,
            String scientificName,
            String description
    ) {}
}