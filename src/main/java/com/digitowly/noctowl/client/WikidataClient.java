package com.digitowly.noctowl.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
@RequiredArgsConstructor
public class WikidataClient {
    @Value("${wikidata.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public String getEntity(String entityId) {
        var url = baseUrl + "/page/summary/" + entityId;
        log.info("Fetching wikidata for entity {} on {}", entityId, url);
        return restTemplate.getForObject(url, String.class);
    }
}
