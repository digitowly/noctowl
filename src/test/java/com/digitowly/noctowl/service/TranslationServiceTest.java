package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.wikidata.WikipediaSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TranslationServiceTest {

    private TranslationService translationService;
    private WikipediaClient wikipediaClient;

    @BeforeEach
    void setUp() {
        this.wikipediaClient = mock(WikipediaClient.class);
        this.translationService = new TranslationService(wikipediaClient);
    }

    @Test
    void getTranslations() {
        var title = "Strix aluco";

        when(wikipediaClient.getSummary(title, LanguageType.EN)).thenReturn(
                new WikipediaSummaryDto(
                        "Tawny owl",
                        "Q25756"
                )
        );

        var result = translationService.getTranslations(title);
        assertEquals("Tawny owl", result.get(LanguageType.EN));
        assertEquals("", result.get(LanguageType.DE));
    }
}