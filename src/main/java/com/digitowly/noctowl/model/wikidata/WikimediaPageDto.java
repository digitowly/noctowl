package com.digitowly.noctowl.model.wikidata;

public record WikimediaPageDto(
        Integer id,
        String key,
        String title,
        String excerpt,
        String description
){}
