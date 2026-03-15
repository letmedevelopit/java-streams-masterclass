<div align="center">

# ☕ java-streams-masterclass

**A complete, runnable Java Streams reference built around a real Order Management domain.**  
No frameworks. No build tools. Just a JDK and one command.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](#contributing)
[![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-brightgreen?style=flat-square)](#contributing)

[Quick Start](#-quick-start) · [What You'll Learn](#-what-youll-learn) · [Project Structure](#-project-structure) · [Concept Reference](#-concept-reference) · [Contributing](#-contributing)

</div>

---

## 🚀 Quick Start

> Requires **JDK 17+** — [Download from Adoptium](https://adoptium.net)

```bash
git clone https://github.com/YOUR_USERNAME/java-streams-masterclass.git
cd java-streams-masterclass
./run.sh
```

That's it. The script compiles all sources and runs 9 labelled demo sections in your terminal.

<details>
<summary>Manual build (without run.sh)</summary>

```bash
mkdir -p out
find src -name "*.java" | xargs javac -d out
java -cp out com.streams.Main
```
</details>

<details>
<summary>Install JDK if needed</summary>

```bash
# Ubuntu / Debian
sudo apt install openjdk-21-jdk

# macOS
brew install openjdk@21

# Windows — https://adoptium.net
```
</details>

---

## 🎯 What You'll Learn

This project covers **every** commonly used Stream operation through a realistic domain — a list of `Order` objects with customers, categories, statuses, amounts, and nested `Item` lists.

| Area | Operations Covered |
|---|---|
| **Filtering** | `filter()`, chained filters, `count()` |
| **Mapping** | `map()`, `mapToDouble/Int/Long()`, `flatMap()` |
| **Sorting & Slicing** | `sorted()`, multi-level `Comparator`, `limit()`, `skip()` |
| **Aggregation** | `sum()`, `average()`, `reduce()`, `min()`, `max()`, `summaryStatistics()` |
| **Matching & Search** | `anyMatch()`, `allMatch()`, `noneMatch()`, `findFirst()`, `Optional` chaining |
| **Collecting** | `toList()`, `toSet()`, `toMap()`, `toMap()` with merge |
| **Grouping** | `groupingBy()`, multi-level grouping, `partitioningBy()` |
| **Downstream collectors** | `counting()`, `summingDouble()`, `averagingDouble()`, `joining()` |
| **Advanced** | Entry stream sorting, two-pass pipelines, date-based filtering, `parallelStream()` |

---

## 📁 Project Structure

```
java-streams-masterclass/
├── run.sh                                      ← compile & run in one command
├── .gitignore
└── src/main/java/com/streams/
    ├── Main.java                               ← entry point, 9 demo sections
    ├── model/
    │   ├── Order.java                          ← core domain object
    │   └── Item.java                           ← order line item
    ├── service/
    │   └── OrderService.java                   ← 29 focused stream methods
    ├── reports/
    │   └── ReportService.java                  ← 8 advanced analytics methods
    └── utils/
        ├── DataSeeder.java                     ← 20 realistic sample orders
        └── Printer.java                        ← console formatting helpers
```

---

## 📖 Concept Reference

### OrderService.java — core operations (29 methods)

Every method is a focused, self-contained stream example with a comment naming the exact concepts used.

| Method | Stream Concepts |
|---|---|
| `getDeliveredOrders()` | `filter()` |
| `getPremiumDeliveredOrders()` | chained `filter()` |
| `countByStatus(status)` | `filter()` + `count()` |
| `getAllCustomerNames()` | `map()` + `distinct()` + `sorted()` |
| `getOrderSummaries()` | `map()` to formatted String |
| `getAllItemsFromDeliveredOrders()` | `flatMap()` |
| `getAllDistinctSkus()` | `flatMap()` + `distinct()` + `sorted()` |
| `getTopOrdersByAmount(n)` | `sorted(reversed)` + `limit()` |
| `getOrdersSortedByCityThenAmount()` | multi-level `Comparator` |
| `getPage(page, size)` | `skip()` + `limit()` |
| `getTotalRevenue()` | `mapToDouble()` + `sum()` |
| `getAverageOrderValue()` | `mapToDouble()` + `average()` → `OptionalDouble` |
| `getTotalQuantityOrdered()` | `mapToInt()` + `reduce()` |
| `getHighestValueOrder()` | `max()` → `Optional<Order>` |
| `getCheapestDeliveredOrder()` | `min()` + `filter()` |
| `hasHighValueOrder(threshold)` | `anyMatch()` |
| `areAllPremiumOrdersDelivered()` | `filter()` + `allMatch()` |
| `hasNoCancelledPremiumOrders()` | `filter()` + `noneMatch()` |
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

### ReportService.java — advanced patterns (8 methods)

| Method | Concept |
|---|---|
| `getMonthlyRevenue()` | `groupingBy` with derived key (`Month`) |
| `getCityLeaderboard()` | `groupingBy` → entry stream → sort → format |
| `getMostPopularItem()` | `flatMap` + `groupingBy` + `max` on entries |
| `getCategoryHealthReport()` | multiple collectors combined |
| `getRecentOrders(days)` | two-stream pipeline with date comparison |
| `getFullyCancelledCustomers()` | `groupingBy` → filter with nested `allMatch` |
| `getCategoryRevenueShare()` | two-pass stream, `LinkedHashMap` for insertion order |
| `getTotalRevenueParallel()` | `parallelStream()` |

---

## 🧠 The Mental Model

```
Raw Data  ──►  [conveyor belt]  ──►  filter  ──►  transform  ──►  Result
```

Every stream has exactly **3 parts**:

```java
collection
    .stream()                          // 1. SOURCE   — open the tap
    .filter(o -> o.isActive())         // 2. MIDDLE   — shape the data (lazy)
    .map(Order::getName)               //              chain as many as needed
    .collect(Collectors.toList());     // 3. TERMINAL — pull the trigger
```

> **The golden rule:** Nothing runs until a terminal operation is called.  
> Middle operations are lazy — they describe *what* to do, not *when*.

### Quick cheat sheet

```
SOURCE            INTERMEDIATE (lazy, chainable)     TERMINAL (triggers execution)
────────────      ──────────────────────────────     ─────────────────────────────
.stream()         .filter(predicate)                 .collect(toList/toSet/toMap)
.parallelStream() .map(function)                     .collect(groupingBy/joining)
Stream.of(...)    .flatMap(function)                 .forEach(consumer)
Arrays.stream()   .sorted(comparator)                .reduce(identity, fn)
IntStream.range() .distinct()                        .count()
                  .limit(n) / .skip(n)               .findFirst() / .findAny()
                  .peek(consumer)                    .anyMatch / allMatch / noneMatch
                  .mapToInt/Double/Long()             .min(comp) / .max(comp)
                                                     .sum() / .average()
```

---

## 🤝 Contributing

Contributions are very welcome! Here are some ideas:

- Add more stream scenarios (e.g. `Collectors.teeing`, `Stream.iterate`, custom collectors)
- Add JUnit 5 tests for every service method
- Add a Maven or Gradle build file
- Add more domain classes (e.g. `Customer`, `Product`, `Invoice`)
- Fix a bug or improve a comment/explanation

### How to contribute

```bash
# 1. Fork the repo on GitHub

# 2. Clone your fork
git clone https://github.com/letmedevelopit/java-streams-masterclass.git

# 3. Create a feature branch
git checkout -b feat/add-teeing-collector-example

# 4. Make your changes, then commit
git commit -m "feat: add Collectors.teeing() example in ReportService"

# 5. Push and open a Pull Request
git push origin feat/add-teeing-collector-example
```

Please keep contributions focused — one concept or fix per PR makes review easy.

---

## 📄 License

MIT — use freely for learning, interviews, or teaching others.

---

<div align="center">

Made with ☕ and a lot of `.stream()` calls.  
If this helped you, consider giving it a ⭐ — it helps others find it!

</div>
