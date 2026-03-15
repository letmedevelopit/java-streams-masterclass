package com.streams.reports;

import com.streams.model.Item;
import com.streams.model.Order;

import java.time.Month;
import java.util.*;
import java.util.stream.*;

/**
 * ReportService — advanced stream patterns for real reporting needs.
 *
 * Extra concepts: collectors chaining, multi-level grouping,
 * stream of map entries, parallel streams, custom collectors.
 */
public class ReportService {

    private final List<Order> orders;

    public ReportService(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Monthly revenue breakdown.
     * Demonstrates: groupingBy with custom key (month), summingDouble.
     */
    public Map<Month, Double> getMonthlyRevenue() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().getMonth(),
                        Collectors.summingDouble(Order::getAmount)
                ));
    }

    /**
     * City leaderboard: cities ranked by total revenue.
     * Demonstrates: groupingBy → entry stream → sort → limit.
     */
    public List<String> getCityLeaderboard() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(
                        Order::getCity,
                        Collectors.summingDouble(Order::getAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> String.format("%-15s ₹%,8.0f", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Most popular item by total quantity sold.
     * Demonstrates: flatMap across orders, groupingBy item name, summingInt.
     */
    public Optional<Map.Entry<String, Integer>> getMostPopularItem() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        Item::getName,
                        Collectors.summingInt(Item::getQuantity)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }

    /**
     * Category health report: for each category, show order count,
     * total revenue, and average order value — all in one pass.
     * Demonstrates: teeing collector (Java 12+) / manual grouping stats.
     */
    public Map<String, String> getCategoryHealthReport() {
        Map<String, Long>   counts  = orders.stream()
                .collect(Collectors.groupingBy(Order::getCategory, Collectors.counting()));
        Map<String, Double> revenue = orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(Order::getCategory, Collectors.summingDouble(Order::getAmount)));
        Map<String, Double> avgs    = orders.stream()
                .collect(Collectors.groupingBy(Order::getCategory, Collectors.averagingDouble(Order::getAmount)));

        return counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.format("orders=%-3d  revenue=₹%,8.0f  avg=₹%,6.0f",
                                e.getValue(),
                                revenue.getOrDefault(e.getKey(), 0.0),
                                avgs.getOrDefault(e.getKey(), 0.0))
                ));
    }

    /**
     * Orders placed in the last N days (from most recent order date in data).
     * Demonstrates: filter with date comparison, sorted by date.
     */
    public List<Order> getRecentOrders(int days) {
        // find the latest date in the dataset
        Optional<Order> latest = orders.stream()
                .max(Comparator.comparing(Order::getOrderDate));

        if (latest.isEmpty()) return List.of();

        return orders.stream()
                .filter(o -> !o.getOrderDate()
                        .isBefore(latest.get().getOrderDate().minusDays(days)))
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Customers with ALL their orders cancelled.
     * Demonstrates: groupingBy → filter on grouped result.
     */
    public List<String> getFullyCancelledCustomers() {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer))
                .entrySet().stream()
                .filter(e -> e.getValue().stream()
                        .allMatch(o -> o.getStatus().equals("CANCELLED")))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Revenue percentage contribution per category.
     * Demonstrates: two-pass stream — first total, then percentage.
     */
    public Map<String, String> getCategoryRevenueShare() {
        double total = orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .mapToDouble(Order::getAmount)
                .sum();

        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(
                        Order::getCategory,
                        Collectors.summingDouble(Order::getAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.format("₹%,8.0f  (%.1f%%)", e.getValue(), (e.getValue() / total) * 100),
                        (a, b) -> a,
                        LinkedHashMap::new   // preserve insertion order
                ));
    }

    /**
     * Parallel stream demo — safe for stateless operations on large data.
     * Demonstrates: parallelStream(), thread-safe collectors.
     */
    public double getTotalRevenueParallel() {
        return orders.parallelStream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .mapToDouble(Order::getAmount)
                .sum();                       // sum() is safe in parallel
    }
}
