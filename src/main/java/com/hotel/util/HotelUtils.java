package com.hotel.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HotelUtils {


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static LocalDate parseStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public static String parseTimestampToString(Timestamp timestamp) {
        return dateFormat.format(timestamp);
    }
}
