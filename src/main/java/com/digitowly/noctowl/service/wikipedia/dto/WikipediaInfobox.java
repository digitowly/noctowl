package com.digitowly.noctowl.service.wikipedia.dto;

import com.digitowly.noctowl.model.enums.ConservationStatus;
import lombok.Builder;

import java.util.Optional;

@Builder
public record WikipediaInfobox(
        String scientificName,
        String commonName,
        Optional<ConservationStatus> conservationStatus,
        Optional<Taxonomy> taxonomy
) {
    @Builder
    public record Taxonomy(
            Optional<Element> kingdom,
            Optional<Element> phylum,
            Optional<Element> taxonomyClass,
            Optional<Element> order,
            Optional<Element> family,
            Optional<Element> genus
    ) {
        @Builder
        public record Element(
                String scientificName,
                String commonName
        ) {
        }
    }
}
