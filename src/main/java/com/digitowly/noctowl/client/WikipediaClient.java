package com.digitowly.noctowl.client;

import com.digitowly.noctowl.model.wikidata.WikipediaSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class WikipediaClient {
    @Value("${wikipedia.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public WikipediaSummaryDto getSummary(String title) {
        var encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        var url = baseUrl + "/page/summary/" + encodedTitle;
        log.info("Fetching Wikipedia Summary for title {} on {}", title, url);
        try {
            return restTemplate.getForObject(url, WikipediaSummaryDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch Wikipedia Summary for title {} on {}", title, url, e);
            return null;
        }
    }
}
