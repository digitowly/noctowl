package com.digitowly.noctowl.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LatinNameExtractorTest {

    @Test
    void extract_with_result() {
        var excerpt = "\"The <span class=\\\"searchmatch\\\">tawny</span> <span class=\\\"searchmatch\\\">owl</span> (Strix aluco), also called the brown <span class=\\\"searchmatch\\\">owl</span>, is a stocky, medium-sized <span class=\\\"searchmatch\\\">owl</span> in the family Strigidae. It is commonly found in woodlands across\",\n";
        var result = LatinNameExtractor.extract(excerpt);
        assertEquals("Strix aluco", result);
    }

    @Test
    void extract_without_result() {
        var excerpt = "This has no latin name.";
        var result = LatinNameExtractor.extract(excerpt);
        assertEquals("", result);
    }
}