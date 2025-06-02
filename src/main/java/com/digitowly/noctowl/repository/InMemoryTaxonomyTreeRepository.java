package com.digitowly.noctowl.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTaxonomyTreeRepository implements TaxonomyTreeRepository {

    private final Set<String> wikiAnimalIds = ConcurrentHashMap.newKeySet();

    @Override
    public void addWikiAnimalIds(@NotNull Set<String> animalIds) {
        this.wikiAnimalIds.addAll(animalIds);
    }

    @Override
    public boolean hasWikiAnimalId(String animalId) {
        return this.wikiAnimalIds.contains(animalId);
    }

}

