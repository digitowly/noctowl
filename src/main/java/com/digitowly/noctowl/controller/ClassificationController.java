package com.digitowly.noctowl.controller;

import com.digitowly.noctowl.model.SpeciesResponse;
import com.digitowly.noctowl.service.SpeciesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/taxon")
@AllArgsConstructor
public class ClassificationController {

    private SpeciesService speciesService;

    // TODO: {lang}/{type}/find/specific
    //  controller with simple taxonomy and scrape result -> name is specific (scientific or common english)

    @GetMapping(value = "{lang}/{type}/find/unspecific")
    public SpeciesResponse findAnimal(
            @PathVariable(name = "lang") String lang,
            @PathVariable(name = "type") String type,

            @RequestParam(name = "name") String name
    ) {
        return speciesService.findByUnspecificName(name, type, lang);
    }
}
