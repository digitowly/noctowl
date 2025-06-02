package com.digitowly.noctowl.service.storage;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class InMemoryTaxonomyTree {

    private final Set<String> wikiAnimalIds = ConcurrentHashMap.newKeySet();

    public void addWikiAnimalIds(@NotNull Set<String> animalIds) {
        this.wikiAnimalIds.addAll(animalIds);
    }

    public boolean hasWikiAnimalId(String animalId) {
        return this.wikiAnimalIds.contains(animalId);
    }

}

