package com.sixestates.dao;

import java.util.concurrent.ConcurrentHashMap;

public class Storeage {
    public static ConcurrentHashMap<String, String> jsonMap;
    public static ConcurrentHashMap<String, byte[]> fileMap;
    public static ConcurrentHashMap<String, Integer> taskMap;

    public static void init() {
        jsonMap = new ConcurrentHashMap<String, String>();
        fileMap = new ConcurrentHashMap<String, byte[]>();
        taskMap = new ConcurrentHashMap<String, Integer>();
    }
}