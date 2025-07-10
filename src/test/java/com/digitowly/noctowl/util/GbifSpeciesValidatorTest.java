package com.digitowly.noctowl.util;

import com.digitowly.noctowl.model.dto.gbif.GbifSpeciesResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GbifSpeciesValidatorTest {

    @Test
    void validate_with_valid_species() {
        var speciesResponseDto = new GbifSpeciesResponseDto(
                new GbifSpeciesResponseDto.Usage(
                        "9282206",
                        "Strix aluco Linnaeus, 1758",
                        "Strix aluco"
                ),
                new GbifSpeciesResponseDto.Diagnostics(
                        "EXACT",
                        99
                )
        );

        var result = GbifSpeciesValidator.validate(speciesResponseDto);
        assertTrue(result);
    }

    @Test
    void validate_with_invalid_matchType() {
        var speciesResponseDto = new GbifSpeciesResponseDto(
                new GbifSpeciesResponseDto.Usage(
                        "9282206",
                        "Strix aluco Linnaeus, 1758",
                        "Strix aluco"
                ),
                new GbifSpeciesResponseDto.Diagnostics(
                        null,
                        99
                )
        );

        var result = GbifSpeciesValidator.validate(speciesResponseDto);
        assertFalse(result);
    }

    @Test
    void validate_with_empty_matchType() {
        var speciesResponseDto = new GbifSpeciesResponseDto(
                new GbifSpeciesResponseDto.Usage(
                        "9282206",
                        "Strix aluco Linnaeus, 1758",
                        "Strix aluco"
                ),
                new GbifSpeciesResponseDto.Diagnostics(
                        "",
                        99
                )
        );

        var result = GbifSpeciesValidator.validate(speciesResponseDto);
        assertFalse(result);
    }

    @Test
    void validate_with_invalid_diagnostics() {
        var speciesResponseDto = new GbifSpeciesResponseDto(
                new GbifSpeciesResponseDto.Usage(
                        "9282206",
                        "Strix aluco Linnaeus, 1758",
                        "Strix aluco"
                ),
                null
        );

        var result = GbifSpeciesValidator.validate(speciesResponseDto);
        assertFalse(result);
    }

    @Test
    void validate_with_invalid_response() {
        var result = GbifSpeciesValidator.validate(null);
        assertFalse(result);
    }
}