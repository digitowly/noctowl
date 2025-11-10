package com.digitowly.noctowl.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageType {
    /**
     * English
     */
    EN("en", "english"),

    /**
     * German
     */
    DE("de", "deutsch"),

    /**
     * French
     */
    FR("fr", "français"),

    /**
     * Spanish
     */
    ES("es", "español"),

    /**
     * Italian
     */
    IT("it", "italiano"),

    /**
     * Portuguese
     */
    PT("pt", "português"),

    /**
     * Dutch
     */
    NL("nl", "nederlands"),

    /**
     * Polish
     */
    PL("pl", "polski"),

    /**
     * Russian
     */
    RU("ru", "русский"),

    /**
     * Japanese
     */
    JA("ja", "日本語"),

    /**
     * Chinese
     */
    ZH("zh", "中文"),

    /**
     * Arabic
     */
    AR("ar", "العربية"),

    /**
     * Turkish
     */
    TR("tr", "türkçe"),

    /**
     * Swedish
     */
    SV("sv", "svenska"),

    /**
     * Norwegian
     */
    NO("no", "norsk"),

    /**
     * Danish
     */
    DA("da", "dansk"),

    /**
     * Finnish
     */
    FI("fi", "suomi"),

    /**
     * Czech
     */
    CS("cs", "čeština"),

    /**
     * Hungarian
     */
    HU("hu", "magyar"),

    /**
     * Romanian
     */
    RO("ro", "română"),

    /**
     * Ukrainian
     */
    UK("uk", "українська"),

    /**
     * Greece
     */
    EL("el", "Ελληνικά");

    private final String name;
    private final String fullName;
}
