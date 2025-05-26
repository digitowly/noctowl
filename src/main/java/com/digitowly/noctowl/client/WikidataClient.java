package com.digitowly.noctowl.client;

import com.digitowly.noctowl.client.dto.WikipediaSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WikidataClient {
    @Value("${spring.wikidata.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public WikipediaSummaryDto getEntity(String name) {
        var encodedTitle = URLEncoder.encode(name, StandardCharsets.UTF_8);
        var url = baseUrl + "/page/summary/" + encodedTitle;
        log.info("Fetching wikidata for entity {} on {}", name, url);
        return restTemplate.getForObject(url, WikipediaSummaryDto.class);
    }
}
