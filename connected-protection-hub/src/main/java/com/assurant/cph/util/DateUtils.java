package com.assurant.cph.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER) : null;
    }

    public static boolean isWithinRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}