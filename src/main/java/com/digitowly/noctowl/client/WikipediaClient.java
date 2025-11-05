package com.digitowly.noctowl.client;

import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.wikidata.WikipediaSummaryDto;
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
public class WikipediaClient {
    @Value("${wikipedia.base-url}")
    private String baseUrl;

    @Value("${wikimedia.user-agent}")
    private String userAgent;

    private final RestTemplate restTemplate;

    public WikipediaSummaryDto getSummary(String title, LanguageType languageType) {
        var url = getBaseUrl(languageType) + "/page/summary/" + title;
        log.info("Fetching Wikipedia Summary for title {} on {}", title, url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    WikipediaSummaryDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch Wikipedia Summary for title {} on {}", title, url, e);
            return null;
        }
    }

    private String getBaseUrl(LanguageType languageType) {
        return String.format(baseUrl, languageType.getName());
    }
}
