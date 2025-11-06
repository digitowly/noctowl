package com.digitowly.noctowl.service.scrape.dto;

import com.digitowly.noctowl.model.enums.ConservationStatus;
import lombok.Builder;

@Builder
public record WikipediaInfobox(
        String scientificName,
        String commonName,
        ConservationStatus conservationStatus,
        Taxonomy taxonomy
) {
    @Builder
    public record Taxonomy(
            Element kingdom,
            Element phylum,
            Element taxonomyClass,
            Element order,
            Element family,
            Element genus
    ) {
        @Builder
        public record Element(
                String scientificName,
                String commonName
        ) {
        }
    }
}
