package com.digitowly.noctowl.util;

import com.digitowly.noctowl.model.dto.gbif.GbifSpeciesResponseDto;


public class GbifSpeciesValidator {

    public static boolean validate(GbifSpeciesResponseDto responseDto) {
        if (responseDto == null ||
                responseDto.diagnostics() == null ||
                responseDto.diagnostics().matchType() == null
        ) {
            return false;
        }

        return switch (responseDto.diagnostics().matchType()) {
            case "EXACT" -> true;
            default -> false;
        };
    }
}
