package com.digitowly.noctowl.controller;

import com.digitowly.noctowl.model.SpeciesResponse;
import com.digitowly.noctowl.service.SpeciesService;
import com.digitowly.noctowl.service.dto.FindSpeciesParams;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/taxon")
@AllArgsConstructor
public class ClassificationController {

    private final SpeciesService speciesService;

    @GetMapping(value = "{lang}/{type}/find/scientific")
    public SpeciesResponse findAnimalByScientificName(
            @PathVariable(name = "lang") String lang,
            @PathVariable(name = "type") String type,

            @RequestParam(name = "name") String name
    ) {
        var params = new FindSpeciesParams(lang, type, name);
        return speciesService.findByScientificName(params);
    }

    @GetMapping(value = "{lang}/{type}/find/common")
    public SpeciesResponse findAnimalByCommonName(
            @PathVariable(name = "lang") String lang,
            @PathVariable(name = "type") String type,

            @RequestParam(name = "name") String name
    ) {
        var params = new FindSpeciesParams(lang, type, name);
        return speciesService.findByCommonName(params);
    }
}
