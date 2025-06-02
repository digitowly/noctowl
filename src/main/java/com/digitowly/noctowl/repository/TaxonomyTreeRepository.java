package com.digitowly.noctowl.repository;

import java.util.Set;

public interface TaxonomyTreeRepository {

    void addWikiAnimalIds(Set<String> animalIds);

    boolean hasWikiAnimalId(String animalId);
}
