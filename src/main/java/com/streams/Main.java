package com.streams;

import com.streams.model.Order;
import com.streams.reports.ReportService;
import com.streams.service.OrderService;
import com.streams.utils.DataSeeder;
import com.streams.utils.Printer;

import java.util.List;
import java.util.Map;

/**
 * Main.java — runs every stream demo in sequence.
 *
 * Just compile and run:
 *   javac -d out $(find src -name "*.java")
 *   java -cp out com.streams.Main
 */
public class Main {

    public static void main(String[] args) {

        List<Order>   orders  = DataSeeder.getOrders();
        OrderService  svc     = new OrderService(orders);
        ReportService reports = new ReportService(orders);

        // ═══════════════════════════════════════════════
        Printer.header("JAVA STREAMS — ORDER MANAGEMENT DEMO");
        System.out.printf("  Loaded %d orders across %d customers%n",
                orders.size(),
                svc.getAllCustomerNames().size());

        // ═══════════════════════════════════════════════
        Printer.header("1. FILTER  — narrowing the stream");

        Printer.section("Delivered orders");
        svc.getDeliveredOrders().forEach(o -> System.out.println("  " + o));

        Printer.section("Count per status  [filter + count]");
        Printer.result("DELIVERED count", svc.countByStatus("DELIVERED"));
        Printer.result("PENDING count",   svc.countByStatus("PENDING"));
        Printer.result("CANCELLED count", svc.countByStatus("CANCELLED"));

        Printer.section("Premium + Delivered  [chained filters]");
        svc.getPremiumDeliveredOrders().forEach(o -> System.out.println("  " + o));

        // ═══════════════════════════════════════════════
        Printer.header("2. MAP — transforming elements");

        Printer.section("All customer names  [map + distinct + sorted]");
        Printer.list("Customers", svc.getAllCustomerNames());

        Printer.section("Order summaries  [map to formatted string]");
        svc.getOrderSummaries().forEach(s -> System.out.println("  " + s));

        // ═══════════════════════════════════════════════
        Printer.header("3. FLATMAP — flattening nested collections");

        Printer.section("All items from delivered orders  [flatMap]");
        svc.getAllItemsFromDeliveredOrders()
                .forEach(item -> System.out.println("  " + item));

        Printer.section("All distinct SKUs  [flatMap + distinct + sorted]");
        Printer.list("SKUs", svc.getAllDistinctSkus());

        // ═══════════════════════════════════════════════
        Printer.header("4. SORTED / LIMIT / SKIP");

        Printer.section("Top 5 orders by amount  [sorted + limit]");
        svc.getTopOrdersByAmount(5)
                .forEach(o -> System.out.printf("  %-10s %-12s ₹%,8.0f%n",
                        o.getId(), o.getCustomer(), o.getAmount()));

        Printer.section("Sorted by city then amount  [multi-level sort]");
        svc.getOrdersSortedByCityThenAmount()
                .forEach(o -> System.out.printf("  %-14s %-10s ₹%,8.0f%n",
                        o.getCity(), o.getCustomer(), o.getAmount()));

        Printer.section("Page 1 (5 per page)  [skip + limit]");
        svc.getPage(1, 5).forEach(o -> System.out.println("  " + o));
        Printer.section("Page 2 (5 per page)");
        svc.getPage(2, 5).forEach(o -> System.out.println("  " + o));

        // ═══════════════════════════════════════════════
        Printer.header("5. AGGREGATION — reduce, sum, average, min, max");

        Printer.section("Revenue & stats  [mapToDouble, reduce, summaryStatistics]");
        Printer.resultF("Total delivered revenue",    svc.getTotalRevenue());
        Printer.result("Average order value",
                String.format("₹%,.0f", svc.getAverageOrderValue().orElse(0)));
        Printer.result("Total items quantity",        (int) svc.getTotalQuantityOrdered());

        svc.getHighestValueOrder().ifPresent(o ->
                Printer.result("Highest value order", o.getId() + " ₹" + String.format("%,.0f", o.getAmount())));
        svc.getCheapestDeliveredOrder().ifPresent(o ->
                Printer.result("Cheapest delivered order", o.getId() + " ₹" + String.format("%,.0f", o.getAmount())));

        var stats = svc.getQuantityStatistics();
        Printer.section("Quantity summary statistics  [summaryStatistics]");
        System.out.printf("  count=%-5d  sum=%-6d  min=%-3d  max=%-3d  avg=%.1f%n",
                stats.getCount(), (long)stats.getSum(), stats.getMin(), stats.getMax(), stats.getAverage());

        // ═══════════════════════════════════════════════
        Printer.header("6. MATCHING & SEARCHING");

        Printer.section("anyMatch / allMatch / noneMatch / findFirst");
        Printer.result("Any order > ₹15,000?",           svc.hasHighValueOrder(15_000));
        Printer.result("All premium orders delivered?",  svc.areAllPremiumOrdersDelivered());
        Printer.result("No cancelled premium orders?",   svc.hasNoCancelledPremiumOrders());
        Printer.result("First pending customer",         svc.findFirstPendingCustomer());

        // ═══════════════════════════════════════════════
        Printer.header("7. GROUPING — groupingBy, partitioningBy");

        Printer.section("Order count by status  [groupingBy + counting]");
        Printer.map("Status counts", svc.getOrderCountByStatus());

        Printer.section("Revenue by category  [groupingBy + summingDouble]");
        svc.getRevenueByCategory().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()))
                .forEach(e -> System.out.printf("  %-15s ₹%,8.0f%n", e.getKey(), e.getValue()));

        Printer.section("Avg order value by customer  [groupingBy + averagingDouble]");
        svc.getAvgOrderValueByCustomer().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()))
                .forEach(e -> System.out.printf("  %-10s ₹%,8.0f%n", e.getKey(), e.getValue()));

        Printer.section("Partition by premium  [partitioningBy]");
        var parts = svc.partitionByPremium();
        Printer.result("Premium orders count",     parts.get(true).size());
        Printer.result("Non-premium orders count", parts.get(false).size());

        Printer.section("Multi-level grouping  [category → status → orders]");
        svc.getOrdersByCategoryAndStatus().forEach((cat, statusMap) -> {
            System.out.println("  " + cat + ":");
            statusMap.forEach((status, list) ->
                    System.out.printf("    %-12s %d orders%n", status, list.size()));
        });

        // ═══════════════════════════════════════════════
        Printer.header("8. JOINING & toMap");

        Printer.section("Premium order report  [joining]");
        System.out.println(svc.getPremiumOrderReport());

        Printer.section("Total spend per customer  [toMap with merge]");
        svc.getTotalSpendPerCustomer().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()))
                .forEach(e -> System.out.printf("  %-10s ₹%,8.0f%n", e.getKey(), e.getValue()));

        Printer.section("Top 3 customers  [entry stream + sort + limit]");
        svc.getTopCustomersBySpend(3).forEach(e ->
                System.out.printf("  %-10s ₹%,8.0f%n", e.getKey(), e.getValue()));

        // ═══════════════════════════════════════════════
        Printer.header("9. ADVANCED REPORTS");

        Printer.section("Monthly revenue  [groupingBy month]");
        reports.getMonthlyRevenue().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-12s ₹%,8.0f%n", e.getKey(), e.getValue()));

        Printer.section("City leaderboard  [groupingBy → entry stream → sort]");
        reports.getCityLeaderboard().forEach(line -> System.out.println("  " + line));

        reports.getMostPopularItem().ifPresent(e ->
                Printer.result("Most popular item [flatMap + groupingBy]",
                        e.getKey() + " (qty: " + e.getValue() + ")"));

        Printer.section("Category health report  [multi-collector]");
        reports.getCategoryHealthReport().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-15s %s%n", e.getKey(), e.getValue()));

        Printer.section("Category revenue share %  [two-pass stream]");
        reports.getCategoryRevenueShare()
                .forEach((cat, info) -> System.out.printf("  %-15s %s%n", cat, info));

        Printer.section("Orders in last 60 days  [filter with date]");
        reports.getRecentOrders(60)
                .forEach(o -> System.out.printf("  %s  %-10s  %s%n",
                        o.getOrderDate(), o.getCustomer(), o.getId()));

        Printer.section("Fully cancelled customers  [groupingBy + allMatch]");
        var cancelled = reports.getFullyCancelledCustomers();
        System.out.println(cancelled.isEmpty() ? "  None" : "  " + cancelled);

        Printer.section("Parallel stream — total revenue");
        Printer.resultF("Parallel result", reports.getTotalRevenueParallel());

        Printer.blank();
        System.out.println("═".repeat(60));
        System.out.println("  All stream demos complete!");
        System.out.println("═".repeat(60));
        Printer.blank();
    }
}
