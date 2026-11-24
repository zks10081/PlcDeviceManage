package com.dm.common.utils.uuid;

import java.util.Date;

public class IdGenerator {

    private static IdGenerator instance = new IdGenerator(0);

    public static IdGenerator initDefaultInstance(int wordId){
        instance = new IdGenerator(wordId);
        return instance;
    }

    private IdGenerator(int machineId) {
        this.machineId = machineId;
    }

    public static IdGenerator getInstance(){
        return instance;
    }

    public static long generateId() {
        return instance.nextId();
    }

    //开始时间戳：2026-11-1 00:00:01
    private final static long startTime = 179346240100L;

    // private final static long TIME_BIT = 40; // max: 2318-06-04
    private final static long MACHINE_BIT = 5; // max 31
    private final static long SEQUENCE_BIT = 8; // 256/10ms

    /**
     * mask/max value
     */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    private long machineId;
    private long sequence = 0L;
    private long lastStmp = -1L;

    public synchronized long nextId(){
        long currStmp = getTimestamp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currStmp = getNextTimestamp();
            }
        } else {
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - startTime) << TIMESTMP_LEFT //
                | machineId << MACHINE_LEFT //
                | sequence;
    }


    public static Long timeGet(){
        return System.currentTimeMillis();
    }

    private long getNextTimestamp() {
        long mill = getTimestamp();
        while (mill <= lastStmp) {
            mill = getTimestamp();
        }
        return mill;
    }

    private long getTimestamp() {
        // per 10ms
        return System.currentTimeMillis() / 10;// 10ms
    }

    public static Date parseIdTimestamp(long id) {
        return new Date((id >>> TIMESTMP_LEFT) * 10);
    }
}
