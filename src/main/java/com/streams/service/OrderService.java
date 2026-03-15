package com.streams.service;

import com.streams.model.Item;
import com.streams.model.Order;

import java.util.*;
import java.util.stream.*;

/**
 * OrderService — every method is a focused stream demo.
 *
 * Concepts covered:
 *   filter, map, flatMap, sorted, distinct, limit, skip,
 *   collect, reduce, count, anyMatch, allMatch, noneMatch,
 *   findFirst, min, max, groupingBy, partitioningBy,
 *   summingDouble, averagingDouble, counting, joining, toMap
 */
public class OrderService {

    private final List<Order> orders;

    public OrderService(List<Order> orders) {
        this.orders = orders;
    }

    // ─────────────────────────────────────────────
    //  BASIC FILTERING
    // ─────────────────────────────────────────────

    /** filter() — keep only delivered orders */
    public List<Order> getDeliveredOrders() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.toList());
    }

    /** filter() chained — premium + delivered */
    public List<Order> getPremiumDeliveredOrders() {
        return orders.stream()
                .filter(Order::isPremium)
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.toList());
    }

    /** filter + count — no collect needed */
    public long countByStatus(String status) {
        return orders.stream()
                .filter(o -> o.getStatus().equals(status))
                .count();
    }

    // ─────────────────────────────────────────────
    //  MAPPING & TRANSFORMATION
    // ─────────────────────────────────────────────

    /** map() — extract customer names */
    public List<String> getAllCustomerNames() {
        return orders.stream()
                .map(Order::getCustomer)
                .distinct()                        // remove duplicates
                .sorted()                          // alphabetical
                .collect(Collectors.toList());
    }

    /** map() — transform objects into summaries */
    public List<String> getOrderSummaries() {
        return orders.stream()
                .map(o -> String.format("%s | %-8s | %-11s | ₹%,6.0f",
                        o.getId(), o.getStatus(), o.getCategory(), o.getAmount()))
                .collect(Collectors.toList());
    }

    /** flatMap() — flatten nested item lists into one stream */
    public List<Item> getAllItemsFromDeliveredOrders() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .flatMap(o -> o.getItems().stream())  // Order → Stream<Item>
                .collect(Collectors.toList());
    }

    /** flatMap() — get all distinct SKUs ever ordered */
    public List<String> getAllDistinctSkus() {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(Item::getSku)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    //  SORTING & SLICING
    // ─────────────────────────────────────────────

    /** sorted() + limit() — top N orders by amount */
    public List<Order> getTopOrdersByAmount(int n) {
        return orders.stream()
                .sorted(Comparator.comparingDouble(Order::getAmount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /** sorted() multi-level — by city, then by amount descending */
    public List<Order> getOrdersSortedByCityThenAmount() {
        return orders.stream()
                .sorted(Comparator.comparing(Order::getCity)
                        .thenComparing(Comparator.comparingDouble(Order::getAmount).reversed()))
                .collect(Collectors.toList());
    }

    /** skip() + limit() — simple pagination */
    public List<Order> getPage(int page, int pageSize) {
        return orders.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    //  AGGREGATION & REDUCTION
    // ─────────────────────────────────────────────

    /** mapToDouble + sum() — total revenue from delivered orders */
    public double getTotalRevenue() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .mapToDouble(Order::getAmount)
                .sum();
    }

    /** mapToDouble + average() — avg order value */
    public OptionalDouble getAverageOrderValue() {
        return orders.stream()
                .mapToDouble(Order::getAmount)
                .average();
    }

    /** reduce() — combine into single value manually */
    public double getTotalQuantityOrdered() {
        return orders.stream()
                .mapToInt(Order::getQuantity)
                .reduce(0, Integer::sum);
    }

    /** max() — single highest-value order */
    public Optional<Order> getHighestValueOrder() {
        return orders.stream()
                .max(Comparator.comparingDouble(Order::getAmount));
    }

    /** min() — cheapest delivered order */
    public Optional<Order> getCheapestDeliveredOrder() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .min(Comparator.comparingDouble(Order::getAmount));
    }

    // ─────────────────────────────────────────────
    //  MATCHING & SEARCHING
    // ─────────────────────────────────────────────

    /** anyMatch() */
    public boolean hasHighValueOrder(double threshold) {
        return orders.stream()
                .anyMatch(o -> o.getAmount() > threshold);
    }

    /** allMatch() */
    public boolean areAllPremiumOrdersDelivered() {
        return orders.stream()
                .filter(Order::isPremium)
                .allMatch(o -> o.getStatus().equals("DELIVERED"));
    }

    /** noneMatch() */
    public boolean hasNoCancelledPremiumOrders() {
        return orders.stream()
                .filter(Order::isPremium)
                .noneMatch(o -> o.getStatus().equals("CANCELLED"));
    }

    /** findFirst() with Optional chaining — no null checks! */
    public String findFirstPendingCustomer() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("PENDING"))
                .findFirst()
                .map(Order::getCustomer)
                .orElse("No pending orders");
    }

    // ─────────────────────────────────────────────
    //  GROUPING & COLLECTING
    // ─────────────────────────────────────────────

    /** groupingBy + counting — order count per status */
    public Map<String, Long> getOrderCountByStatus() {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
    }

    /** groupingBy + summingDouble — revenue per category */
    public Map<String, Double> getRevenueByCategory() {
        return orders.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(
                        Order::getCategory,
                        Collectors.summingDouble(Order::getAmount)
                ));
    }

    /** groupingBy + averagingDouble — avg basket size per customer */
    public Map<String, Double> getAvgOrderValueByCustomer() {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomer,
                        Collectors.averagingDouble(Order::getAmount)
                ));
    }

    /** toMap() — build an id → order lookup map */
    public Map<String, Order> getOrderLookupMap() {
        return orders.stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        o -> o
                ));
    }

    /** toMap() with merge — total spend per customer */
    public Map<String, Double> getTotalSpendPerCustomer() {
        return orders.stream()
                .collect(Collectors.toMap(
                        Order::getCustomer,
                        Order::getAmount,
                        Double::sum           // merge duplicate keys by adding
                ));
    }

    /** partitioningBy() — split into premium vs non-premium */
    public Map<Boolean, List<Order>> partitionByPremium() {
        return orders.stream()
                .collect(Collectors.partitioningBy(Order::isPremium));
    }

    /** joining() — build a formatted report string */
    public String getPremiumOrderReport() {
        return orders.stream()
                .filter(Order::isPremium)
                .sorted(Comparator.comparingDouble(Order::getAmount).reversed())
                .map(o -> String.format("  %s | %-8s | %-10s | ₹%,6.0f",
                        o.getId(), o.getStatus(), o.getCustomer(), o.getAmount()))
                .collect(Collectors.joining("\n"));
    }

    /** Multi-level groupingBy — category → status → orders */
    public Map<String, Map<String, List<Order>>> getOrdersByCategoryAndStatus() {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCategory,
                        Collectors.groupingBy(Order::getStatus)
                ));
    }

    /** Top N customers by spend — two-step stream pipeline */
    public List<Map.Entry<String, Double>> getTopCustomersBySpend(int n) {
        return getTotalSpendPerCustomer()
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
    }

    /** summaryStatistics() — all numeric stats in one pass */
    public IntSummaryStatistics getQuantityStatistics() {
        return orders.stream()
                .mapToInt(Order::getQuantity)
                .summaryStatistics();
    }
}
