package com.digitowly.noctowl.service.storage;

import com.digitowly.noctowl.model.entity.TaxonomyTreeEntity;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.model.enums.wikidata.WikidataQID;
import com.digitowly.noctowl.repository.taxonomytree.TaxonomyTreeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class TaxonomyTreeStorageHandler {

    private final TaxonomyTreeRepository treeRepository;

    public boolean isTaxonCached(@NotNull TaxonType taxonType, String wikidataId) {
        var entity = treeRepository.findByRoot(taxonType.getWikidataQID().getId()).orElse(null);
        if (entity == null) return false;

        var children = entity.getChildren();
        if (children == null) return false;

        var isCached = children.contains(wikidataId);
        if (isCached) log.info("Cache hit: {} is a known {}.", wikidataId, taxonType);
        return isCached;
    }

    public void storeChildren(@NotNull WikidataQID root, Set<String> children) {
        var childrenString = String.join(",", children);
        var entity = treeRepository.findByRoot(root.getId()).orElse(null);
        if (entity == null) {
            var newEntity = TaxonomyTreeEntity.builder()
                    .root(root.getId())
                    .children(childrenString)
                    .build();
            treeRepository.save(newEntity);
            return;
        }

        var updatedChildren = entity.getChildren().concat(childrenString);
        treeRepository.updateChildren(root.getId(), updatedChildren);
    }
}
