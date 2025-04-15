package com.muzi.part1.utils;

import java.util.UUID;

public class IdUtils {

    public static String generateId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
