package com.digitowly.noctowl.repository.taxonomytree;

import com.digitowly.noctowl.model.entity.TaxonomyTreeEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface TaxonomyTreeRepository extends CrudRepository<TaxonomyTreeEntity, Long> {
    Optional<TaxonomyTreeEntity> findByRoot(String root);

    @Transactional
    @Modifying
    @Query("update taxonomy_trees t set t.children = ?1 where t.root = ?2")
    void updateChildren(String rootId, String children);
}
