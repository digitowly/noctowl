package com.digitowly.noctowl.service;

import com.digitowly.noctowl.client.WikipediaClient;
import com.digitowly.noctowl.model.enums.LanguageType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TranslationService {

    private final WikipediaClient wikipediaClient;

    public Map<LanguageType, String> getTranslations(String scientificName) {
        var translations = new HashMap<LanguageType, String>();
        log.info("Getting translations for {}", scientificName);

        for (LanguageType languageType : LanguageType.values()) {
            var summary = wikipediaClient.getSummary(scientificName, languageType);
            if (summary == null) {
                translations.put(languageType, "");
                log.warn("No summary found for {}", languageType);
                continue;
            }
            translations.put(languageType, summary.title());
        }
        return translations;
    }
}
