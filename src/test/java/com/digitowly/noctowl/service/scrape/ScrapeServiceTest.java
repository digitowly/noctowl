package com.digitowly.noctowl.service.scrape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScrapeServiceTest {

    @Test
    void getPage() {
        var url = "https://example.com";
        var scrapeService = new ScrapeService();
        var result = scrapeService.getPage(url);
        assertNotNull(result);
    }

    @Test
    void getPage_failure() {
        var scrapeService = new ScrapeService();
        var result = scrapeService.getPage(null);
        assertNull(result);
    }
}