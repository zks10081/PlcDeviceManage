package com.dm.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {

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
