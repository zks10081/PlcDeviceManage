package com.ruoyi.common.utils;

import com.ruoyi.common.utils.uuid.IdGenerator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 通用数据处理工具（原 dispatch 项目 com.src.utils.DataUtil，去除 hutool 依赖）
 */
public class DataUtil {

    private static final Random RANDOM = new Random();

    public static String DATE_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_YMD = "yyyy-MM-dd";

    public static Date getDate(String format, String text) {
        try {
            return new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String dateToString(String format, Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static boolean isNotNull(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Id生成器
     * 时间戳(毫秒) + 4位随机数
     * @return 例如：1762175432123456
     */
    public static String timestamp4Random() {
        // 毫秒时间戳
        long ts = System.currentTimeMillis();
        // 0~9999
        int rand = RANDOM.nextInt(10000);
        // 格式化补齐4位，不足前面补0
        return ts + String.format("%04d", rand);
    }

    public static Long snowflake(){
        long id = IdGenerator.generateId();
        return id;
    }

    public static Long snowflakeShort(Long id){
        String strId = String.valueOf(id);
        if(strId.length() < 16){
            for (int i = 0; i<16-strId.length();i++){
                strId = "0"+strId;
            }
        }else {
            strId = strId.substring(strId.length()-16);
        }
        return Long.parseLong(strId);
    }

    /** ---------------------------- 比较大小 ------------------------------------ */

    /** a > b */
    public static boolean gt(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) > 0;
    }

    /** a >= b */
    public static boolean gte(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) >= 0;
    }

    /** a < b */
    public static boolean lt(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) < 0;
    }

    /** a <= b */
    public static boolean lte(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) <= 0;
    }

    /** a == b */
    public static boolean eq(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) == 0;
    }

    /** a != b */
    public static boolean ne(BigDecimal a, BigDecimal b) {
        return !eq(a, b);
    }

    /** ---------------------------- 数据类型相关处理方法 ------------------------------------ */

    /**
     * 集合分片
     * @param list 原始集合
     * @param size 每片多少条
     * @return 分片后的集合
     */
    public static <T> List<List<T>> splitList(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            int end = Math.min(i + size, list.size());
            result.add(list.subList(i, end));
        }
        return result;
    }
}
