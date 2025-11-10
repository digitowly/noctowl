package com.digitowly.noctowl.service.dto;

import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.enums.TaxonCheckStrategy;
import com.digitowly.noctowl.model.enums.TaxonType;
import lombok.Builder;

@Builder
public record FindTaxonomyParams(
        TaxonType type,
        String name,
        Integer entryLimit,
        LanguageType langType,
        TaxonCheckStrategy strategy
) {
}
