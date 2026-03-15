# Java Streams — Complete Working Project

A fully runnable Order Management demo that exercises every Java Streams concept.  
No frameworks, no build tools required — just a JDK.

---

## Project Structure

```
streams-project/
├── run.sh                                  ← compile & run in one command
└── src/main/java/com/streams/
    ├── Main.java                           ← entry point, runs all demos
    ├── model/
    │   ├── Order.java                      ← Order domain object
    │   └── Item.java                       ← Order line item
    ├── service/
    │   └── OrderService.java               ← 25 stream methods, every core op
    ├── reports/
    │   └── ReportService.java              ← advanced patterns & analytics
    └── utils/
        ├── DataSeeder.java                 ← 20 sample orders, 5 customers
        └── Printer.java                    ← console formatting helpers
```

---

## How to Run

### Prerequisites
Java 17 or higher (JDK, not just JRE).

```bash
# Ubuntu / Debian
sudo apt install openjdk-21-jdk

# macOS
brew install openjdk@21

# Windows — download from https://adoptium.net
```

### One command

```bash
./run.sh
```

### Or manually

```bash
mkdir -p out
find src -name "*.java" | xargs javac -d out
java -cp out com.streams.Main
```

---

## What Each File Teaches

### OrderService.java — core operations

| Method | Stream Concepts Used |
|---|---|
| `getDeliveredOrders()` | `filter()` |
| `getPremiumDeliveredOrders()` | chained `filter()` |
| `countByStatus()` | `filter()` + `count()` |
| `getAllCustomerNames()` | `map()` + `distinct()` + `sorted()` |
| `getOrderSummaries()` | `map()` to formatted String |
| `getAllItemsFromDeliveredOrders()` | `flatMap()` |
| `getAllDistinctSkus()` | `flatMap()` + `distinct()` |
| `getTopOrdersByAmount(n)` | `sorted(reversed)` + `limit()` |
| `getOrdersSortedByCityThenAmount()` | multi-level `Comparator` |
| `getPage(page, size)` | `skip()` + `limit()` |
| `getTotalRevenue()` | `mapToDouble()` + `sum()` |
| `getAverageOrderValue()` | `mapToDouble()` + `average()` → `OptionalDouble` |
| `getTotalQuantityOrdered()` | `mapToInt()` + `reduce()` |
| `getHighestValueOrder()` | `max()` → `Optional<Order>` |
| `getCheapestDeliveredOrder()` | `min()` + `filter()` |
| `hasHighValueOrder()` | `anyMatch()` |
| `areAllPremiumOrdersDelivered()` | `allMatch()` |
| `hasNoCancelledPremiumOrders()` | `noneMatch()` |
| `findFirstPendingCustomer()` | `findFirst()` + `Optional.map()` + `orElse()` |
| `getOrderCountByStatus()` | `groupingBy()` + `counting()` |
| `getRevenueByCategory()` | `groupingBy()` + `summingDouble()` |
| `getAvgOrderValueByCustomer()` | `groupingBy()` + `averagingDouble()` |
| `getOrderLookupMap()` | `toMap()` |
| `getTotalSpendPerCustomer()` | `toMap()` with merge function |
| `partitionByPremium()` | `partitioningBy()` |
| `getPremiumOrderReport()` | `joining()` |
| `getOrdersByCategoryAndStatus()` | multi-level `groupingBy()` |
| `getTopCustomersBySpend(n)` | entry stream + sort + `limit()` |
| `getQuantityStatistics()` | `summaryStatistics()` |

### ReportService.java — advanced patterns

| Method | Concept |
|---|---|
| `getMonthlyRevenue()` | `groupingBy` with derived key (Month) |
| `getCityLeaderboard()` | `groupingBy` → entry stream → format |
| `getMostPopularItem()` | `flatMap` + `groupingBy` + `max` on entries |
| `getCategoryHealthReport()` | multiple collectors, merge results |
| `getRecentOrders(days)` | two-stream max-find then filter with dates |
| `getFullyCancelledCustomers()` | `groupingBy` → filter with nested `allMatch` |
| `getCategoryRevenueShare()` | two-pass stream, `LinkedHashMap` for order |
| `getTotalRevenueParallel()` | `parallelStream()` |

---

## Key Concepts Quick Reference

```
Source          Intermediate (lazy)           Terminal (triggers execution)
──────────      ────────────────────────      ───────────────────────────────
.stream()       .filter(predicate)            .collect(toList/toSet/toMap)
.parallelStream .map(function)                .collect(groupingBy/joining)
Stream.of(...)  .flatMap(function)            .forEach(consumer)
Arrays.stream() .sorted(comparator)           .reduce(identity, accumulator)
IntStream.range .distinct()                   .count()
                .limit(n) / .skip(n)          .findFirst() / .findAny()
                .peek(consumer)               .anyMatch / allMatch / noneMatch
                .mapToInt/Double/Long         .min(comp) / .max(comp)
                                              .sum() / .average()
```

### The golden rule
> Nothing runs until a terminal operation is called.  
> Intermediate operations are lazy — they describe what to do, not when.

---

*Built as a companion to the Java Streams Study Guide.*
