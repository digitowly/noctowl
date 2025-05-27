package com.digitowly.noctowl.model.wikidata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WikidataProperty {
    PARENT_TAXON("P171"),
    SUBCLASS_OF("P279");

    private final String id;
}
