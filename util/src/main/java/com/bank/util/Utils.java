package com.bank.util;

import org.apache.commons.lang3.tuple.Pair;

public final class Utils {
    private Utils() {
    }

    public static int sum(int a, int b) {
        return a + b;
    }

    private static final Pair<String, String> COLOR_RANGE = Pair.of("red", "purple");

    public static String colorRange() {
        return COLOR_RANGE.getLeft() + " to " + COLOR_RANGE.getRight();
    }
}
