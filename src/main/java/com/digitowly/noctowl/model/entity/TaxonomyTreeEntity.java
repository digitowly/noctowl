package com.digitowly.noctowl.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "taxonomy_trees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyTreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "root")
    private String root;

    @Column(name = "children")
    private String children;
}
