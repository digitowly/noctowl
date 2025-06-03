package com.digitowly.noctowl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "taxonomy_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyEntryEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "response")
    private String response;
}
