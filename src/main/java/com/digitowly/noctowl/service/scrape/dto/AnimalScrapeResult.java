package com.digitowly.noctowl.service.scrape.dto;

import lombok.Builder;

@Builder
public record AnimalScrapeResult(
        WikipediaInfobox wikipediaInfobox
) {
}
