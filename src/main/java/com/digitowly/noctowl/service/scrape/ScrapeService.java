package com.digitowly.noctowl.service.scrape;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScrapeService {

    public Document getPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            log.error("Failed to fetch page {}: {}", url, e.getMessage(), e);
            return null;
        }
    }
}
