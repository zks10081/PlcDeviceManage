package com.ruoyi.common.utils;

import java.math.BigDecimal;
import java.util.Random;

public class MathUtils {

    private static final Random RANDOM = new Random();

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

    /***
     * a > b
     * @param a
     * @param b
     * @return
     */
    public static boolean gt(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) > 0;
    }

    /**
     * a >= b
     */
    public static boolean gte(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) >= 0;
    }

    /**
     * a < b
     */
    public static boolean lt(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) < 0;
    }

    /**
     * a <= b
     */
    public static boolean lte(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) <= 0;
    }

    /**
     * a == b
     */
    public static boolean eq(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.compareTo(b) == 0;
    }

    /**
     * a != b
     */
    public static boolean ne(BigDecimal a, BigDecimal b) {
        return !eq(a, b);
    }

}
