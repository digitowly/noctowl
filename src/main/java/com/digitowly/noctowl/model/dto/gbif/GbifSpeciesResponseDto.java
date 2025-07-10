package com.digitowly.noctowl.model.dto.gbif;

public record GbifSpeciesResponseDto(
        Usage usage,
        Diagnostics diagnostics
) {
    public record Usage(
            String key,
            String name,
            String canonicalName
    ){}
    public record Diagnostics(
            String matchType,
            Integer confidence
    ){}
}
