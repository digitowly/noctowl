package com.digitowly.noctowl.client;

import com.digitowly.noctowl.model.wikidata.WikimediaPagesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class WikimediaClient {
    @Value("${wikimedia.base-url}")
    private String baseUrl;

    @Value("${wikimedia.user-agent}")
    private String userAgent;

    private final RestTemplate restTemplate;

    public WikimediaPagesDto getPages(String title) {
        var encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        var url = baseUrl + "/wikipedia/en/search/page?q=" + encodedTitle;
        log.info("Searching for wikipedia pages with the term {} on {}", title, url);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<WikimediaPagesDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                WikimediaPagesDto.class
        );

        return response.getBody();
    }
}
