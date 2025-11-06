package com.digitowly.noctowl.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConservationStatus {
    Extinct("Extinct", "EX"),
    ExtinctInTheWild("Extinct in the Wild", "EW"),
    CriticallyEndangered("Critically Endangered", "CR"),
    Endangered("Endangered", "EN"),
    Vulnerable("Vulnerable", "VU"),
    NearThreatened("Near Threatened", "NT"),
    ConservationDependent("Conservation Dependent", "CD"),
    LeastConcern("Least Concern", "LC"),
    DataDeficient("Data Deficient", "DD"),
    NotEvaluated("Not Evaluated", "NE");

    private final String name;
    private final String code;
}