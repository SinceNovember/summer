package com.summer.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateUtil {
    private static final DateTimeFormatter FORRMATTEER =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.CHINA)
                    .withZone(ZoneId.systemDefault());

    public static String now(){
        return FORRMATTEER.format(Instant.now());
    }
}
