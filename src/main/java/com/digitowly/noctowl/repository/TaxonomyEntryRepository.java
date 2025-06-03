package com.digitowly.noctowl.repository;

import com.digitowly.noctowl.model.entity.TaxonomyEntryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxonomyEntryRepository extends CrudRepository<TaxonomyEntryEntity, String> {
}
