package com.hotel;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HotelUtils {

    public static Timestamp parseStringToTimestamp(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        Date parsedDate = dateFormat.parse(date);
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static String parseTimestampToString(Timestamp timestamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return dateFormat.format(timestamp);
    }
}
