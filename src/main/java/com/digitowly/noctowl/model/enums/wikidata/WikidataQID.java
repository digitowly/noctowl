package com.digitowly.noctowl.model.enums.wikidata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WikidataQID {
    ANIMALIA("Q729"),
    PLANTAE("Q756");

    private final String id;
}
