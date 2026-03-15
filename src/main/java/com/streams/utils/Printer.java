package com.streams.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Simple console formatting helpers so demo output is easy to read.
 */
public class Printer {

    private static final String LINE  = "─".repeat(60);
    private static final String DLINE = "═".repeat(60);

    public static void header(String title) {
        System.out.println("\n" + DLINE);
        System.out.println("  " + title);
        System.out.println(DLINE);
    }

    public static void section(String title) {
        System.out.println("\n" + LINE);
        System.out.println("  " + title);
        System.out.println(LINE);
    }

    public static void result(String label, Object value) {
        System.out.printf("  %-38s %s%n", label + ":", value);
    }

    public static void resultF(String label, double value) {
        System.out.printf("  %-38s ₹%,.0f%n", label + ":", value);
    }

    public static <T> void list(String label, Collection<T> items) {
        System.out.println("  " + label + ":");
        items.forEach(item -> System.out.println("    • " + item));
    }

    public static <K, V> void map(String label, Map<K, V> map) {
        System.out.println("  " + label + ":");
        map.forEach((k, v) -> System.out.printf("    %-20s → %s%n", k, v));
    }

    public static void blank() { System.out.println(); }
}
