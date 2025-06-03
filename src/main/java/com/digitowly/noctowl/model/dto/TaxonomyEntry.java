package com.digitowly.noctowl.model.dto;

import com.digitowly.noctowl.model.enums.TaxonType;

public record TaxonomyEntry(
        TaxonType type,
        WikipediaInfo wikipediaInfo
) {
    public record WikipediaInfo(
            Integer id,
            String title,
            String key,
            String description
    ) {}
}