package com.digitowly.noctowl.model.dto;

import com.digitowly.noctowl.model.enums.TaxonType;
import lombok.Builder;

import java.util.List;

@Builder
public record TaxonomyResponse(
        TaxonType type,
        List<TaxonomyEntry> entries
) {}
