package com.digitowly.noctowl.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
@RequiredArgsConstructor
public class WikidataClient {
    @Value("${wikidata.base-url}")
    private String baseUrl;

    @Value("${wikimedia.user-agent}")
    private String userAgent;

    private final RestTemplate restTemplate;

    public String getEntity(String entityId) {
        var url = baseUrl + "/wiki/Special:EntityData/" + entityId + ".json";
        log.info("Fetching wikidata for entity {} on {}", entityId, url);

        return fetch(url, entityId);
    }

    public String getClaims(String entityId) {
        var url = baseUrl + "/w/api.php?action=wbgetclaims&entity=" + entityId + "&format=json";
        log.info("Fetching claims for entity {} on {}", entityId, url);

        return fetch(url, entityId);
    }

    private String fetch(String url, String entityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);
        var requestEntity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching wikidata for entity {}: {}", entityId, e.getMessage(), e);
        }

        return null;
    }
}
