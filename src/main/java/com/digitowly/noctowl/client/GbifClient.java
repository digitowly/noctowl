package com.digitowly.noctowl.client;

import com.digitowly.noctowl.model.dto.gbif.GbifSpeciesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class GbifClient {
    @Value("${gbif.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public GbifSpeciesResponseDto getSpecies(String genericName) {
        var url = baseUrl + "/species/match?genericName=" + genericName;
        log.info("Fetching Gbif species for generic name {} on {}", genericName, url);
        try {
            return restTemplate.getForObject(url, GbifSpeciesResponseDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch Gbif species for generic name {} on {}", genericName, url, e);
            return null;
        }
    }
}
