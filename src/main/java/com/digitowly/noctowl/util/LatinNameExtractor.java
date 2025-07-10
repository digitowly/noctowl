package com.digitowly.noctowl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatinNameExtractor {
    public static String extract(String description){
        Pattern pattern = Pattern.compile("\\(([^\\)]+)\\)");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
