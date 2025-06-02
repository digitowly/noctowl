package com.digitowly.noctowl.model.enums;

import com.digitowly.noctowl.model.enums.wikidata.WikidataQID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaxonType {
    ANIMAL(WikidataQID.ANIMALIA);
//    PLANT,
//    FUNGI,

    private final WikidataQID wikidataQID;
}
