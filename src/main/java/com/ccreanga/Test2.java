package com.ccreanga;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Test2 {

    public static void main(String[] args) throws Exception {

        System.gc();
        System.gc();
        System.gc();

        long m1 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        HashMap<byte[],byte[]> map = new HashMap<>();
//        for (int i = 0; i < 1000*1000; i++) {
//            map.put(RandomUtils.randomAlphabetic(15).getBytes("UTF-8"),RandomUtils.randomAlphabetic(15).getBytes("UTF-8"));
//        }

        System.gc();
        System.gc();
        System.gc();

        long m2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        System.out.println(map.size());
        System.out.println((m2-m1) / (1024*1024));
        map = null;
        m1 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.gc();
        System.gc();
        System.gc();
        CacheMap fastUtil = new CacheMap();
        for (int i = 0; i < 1000*1000; i++) {
            fastUtil.put(RandomUtils.randomAlphabetic(15).getBytes("UTF-8"),RandomUtils.randomAlphabetic(15).getBytes("UTF-8"));
        }

        System.gc();
        System.gc();
        System.gc();

        m2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println(fastUtil.size());
        System.out.println((m2-m1) / (1024*1024));

    }

}
