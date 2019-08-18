package com.happyslowly.optparser;

public class Helper {
    public static String getOptionFormat(int len) {
        int width = len != 0 ? len + 4 : 0;
        return width == 0 ? "  %s  %s%n" : ("  %-" + width + "s  %s%n");
    }
}
