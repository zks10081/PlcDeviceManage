package com.ruoyi.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间处理工具（原 dispatch 项目 com.src.utils.DateTimeUtil）
 */
public class DateTimeUtil {
    public static SimpleDateFormat sdf;

    public static DateTimeFormatter DATE_YMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

    public static DateTimeFormatter DATE_YMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm");

    public static DateTimeFormatter DATE_YMDH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    public static DateTimeFormatter DATE_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DateTimeFormatter DATE_YM = DateTimeFormatter.ofPattern("yyyy-MM");

    public static DateTimeFormatter DATE_Y = DateTimeFormatter.ofPattern("yyyy");

    public static LocalDateTime toDateY(String date) {
        return toDate(date, "yyyy");
    }

    public static LocalDateTime toDateYM(String date) {
        return toDate(date, "yyyy-MM");
    }

    public static LocalDateTime toDateYMD(String date) { return toDate(date, "yyyy-MM-dd"); }

    public static LocalDateTime toDateYMDHMS(String date) {
        return toDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toDateY(LocalDateTime date) {
        return toDate(date, "yyyy");
    }

    public static String toDateYM(LocalDateTime date) {
        return toDate(date, "yyyy-MM");
    }

    public static String toDateYMD(LocalDateTime date) { return toDate(date, "yyyy-MM-dd"); }

    public static String toDateYMDHMS(LocalDateTime date) {
        return toDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime toDate(String date, String pattern) {
        sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(date);
            return LocalDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date toDate(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }



    public static LocalDateTime toDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static String toDate(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(dateTime);
    }
}
