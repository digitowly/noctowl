package com.digitowly.noctowl.controller;

import com.digitowly.noctowl.model.dto.TaxonomyResponse;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.service.TaxonomyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/taxon")
@AllArgsConstructor
public class ClassificationController {

    private final TaxonomyService taxonomyService;

    @GetMapping(value = "/animals/find")
    public TaxonomyResponse findAnimal(@RequestParam(name = "name") String name) {
        return taxonomyService.find(TaxonType.ANIMAL, name);
    }

    @GetMapping(value = "/plants/find")
    public TaxonomyResponse findPlant(@RequestParam(name = "name") String name) {
        return taxonomyService.find(TaxonType.PLANT, name);
    }
}
