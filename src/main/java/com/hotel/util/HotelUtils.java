package com.hotel.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HotelUtils {

    /*
    * Using Timezone from Montreal. If needed you should configure accordingly to the Hotel timezone.
    * */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Timestamp parseStringToTimestamp(String date) throws ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        Date parsedDate = dateFormat.parse(date);
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static String parseTimestampToString(Timestamp timestamp) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return dateFormat.format(timestamp);
    }
}
