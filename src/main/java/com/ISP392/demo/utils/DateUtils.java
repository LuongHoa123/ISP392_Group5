package com.ISP392.demo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final String DATE_FORMAT="yyyy-MM-dd";

    public static DateTimeFormatter formatter=DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static String toString(LocalDate date) {
        return date.format(formatter);
    }

    public static LocalDate toDate(String date) {
        return LocalDate.parse(date, formatter);
    }
}
