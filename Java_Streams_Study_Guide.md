# Java Streams — Complete Study Guide

> A personal reference built from interactive learning. Covers mental models, all operations, real-world patterns, and interview prep.

---

## Table of Contents

1. [The Mental Model](#1-the-mental-model)
2. [The 3-Part Structure](#2-the-3-part-structure)
3. [The Golden Rule — Laziness](#3-the-golden-rule--laziness)
4. [Intermediate Operations](#4-intermediate-operations)
5. [Terminal Operations](#5-terminal-operations)
6. [The Collectors Toolbox](#6-the-collectors-toolbox)
7. [Real-World Examples — Order System](#7-real-world-examples--order-system)
8. [Common Patterns & Recipes](#8-common-patterns--recipes)
9. [Pitfalls to Avoid](#9-pitfalls-to-avoid)
10. [Interview Cheat Sheet](#10-interview-cheat-sheet)

---

## 1. The Mental Model

Before writing a single line, lock in this picture:

```
Raw Data  →  [conveyor belt]  →  filter  →  transform  →  Result
```

Three analogies that make streams click forever:

| Analogy | Source | Middle Operations | Terminal |
|---|---|---|---|
| 🏭 Factory assembly line | Raw materials enter | Each station does one job | Finished product exits |
| 🚰 Water through pipes | Turn on the tap | Filters & transformers | Collect in a bucket |
| 🎬 Netflix queue | Your full list | Genre filter, sort by rating | Top 5 to watch |

---

## 2. The 3-Part Structure

**Every stream, without exception, has exactly these three parts:**

```java
collection
    .stream()                          // PART 1 — SOURCE: open the tap
    .filter(x -> x.isActive())         // PART 2 — MIDDLE OPS: shape the data
    .map(User::getName)                //           chain as many as you need
    .sorted()                          //
    .collect(Collectors.toList());     // PART 3 — TERMINAL: pull the trigger
```

### Sources

```java
list.stream()                          // from a List or Collection
Set.of("a", "b").stream()             // from a Set
Arrays.stream(array)                   // from an array
Stream.of("x", "y", "z")             // inline values
Stream.iterate(0, n -> n + 1)         // infinite stream (use limit!)
IntStream.range(1, 10)                // numeric range: 1,2,...,9
Files.lines(Path.of("file.txt"))      // lines from a file
```

---

## 3. The Golden Rule — Laziness

> **Nothing runs until a terminal operation is called.**

Middle operations just *describe* what to do. They don't do it yet. The terminal operation fires the entire pipeline at once.

```java
Stream<String> stream = names.stream()
    .filter(n -> {
        System.out.println("filtering: " + n);  // never prints
        return n.length() > 3;
    });

// Nothing has happened yet. No filtering. No printing.

List<String> result = stream.collect(Collectors.toList());
// NOW it runs. Printing happens here.
```

**Why this matters:**
- You can build streams and pass them around before executing
- The JVM can optimise the whole pipeline at once
- Short-circuit operations (`findFirst`, `anyMatch`) stop early — they don't process the entire collection

---

## 4. Intermediate Operations

These return a `Stream<T>`. They are **lazy** — chain as many as you like.

### filter() — keep matching elements

```java
// Keep only adults
List<User> adults = users.stream()
    .filter(u -> u.getAge() >= 18)
    .collect(Collectors.toList());

// Method reference style
List<Order> active = orders.stream()
    .filter(Order::isActive)
    .collect(Collectors.toList());
```

### map() — transform each element (one-to-one)

```java
// Object → String
List<String> names = users.stream()
    .map(User::getName)
    .collect(Collectors.toList());

// String → Integer
List<Integer> lengths = names.stream()
    .map(String::length)
    .collect(Collectors.toList());
```

### flatMap() — map + flatten nested collections

```java
// Each order has a list of items — get ALL items in one flat list
List<Item> allItems = orders.stream()
    .flatMap(order -> order.getItems().stream())
    .collect(Collectors.toList());

// Split sentences into words
List<String> words = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(Collectors.toList());
```

> **Map vs FlatMap:** `map()` gives `Stream<List<Item>>`. `flatMap()` gives `Stream<Item>`. Use `flatMap` when each element maps to a collection.

### sorted() — sort elements

```java
// Natural order
list.stream().sorted()

// Reverse order
list.stream().sorted(Comparator.reverseOrder())

// By field
users.stream().sorted(Comparator.comparing(User::getName))

// By field, descending
orders.stream().sorted(Comparator.comparingDouble(Order::getAmount).reversed())

// Multi-level sort
users.stream().sorted(
    Comparator.comparing(User::getCity)
              .thenComparing(User::getName)
)
```

### distinct() — remove duplicates

```java
List<Integer> unique = numbers.stream()
    .distinct()    // uses .equals() internally
    .collect(Collectors.toList());
```

### limit() and skip() — pagination

```java
// First page (items 1–10)
list.stream().limit(10)

// Second page (items 11–20)
list.stream().skip(10).limit(10)
```

### peek() — inspect without changing (debugging only)

```java
list.stream()
    .filter(n -> n > 5)
    .peek(n -> System.out.println("after filter: " + n))  // for debugging
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

### mapToInt / mapToDouble / mapToLong — switch to primitive streams

```java
// Gives access to sum(), average(), min(), max(), count()
int total = orders.stream()
    .mapToInt(Order::getQuantity)
    .sum();

OptionalDouble avg = scores.stream()
    .mapToDouble(Double::parseDouble)
    .average();
```

---

## 5. Terminal Operations

These **trigger** the pipeline and return a non-stream result. After a terminal op, the stream is consumed and cannot be reused.

### collect() — gather into a collection

See [Section 6](#6-the-collectors-toolbox) for the full Collectors reference.

```java
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.joining(", "))
```

### forEach() — side effect on each element

```java
// Printing, saving to DB, sending events — NOT for building results
users.stream().forEach(System.out::println);

// Prefer forEachOrdered() if order matters in parallel streams
parallelStream.forEachOrdered(System.out::println);
```

### reduce() — fold all elements into one value

```java
// Sum
int sum = numbers.stream().reduce(0, Integer::sum);

// Product
int product = numbers.stream().reduce(1, (a, b) -> a * b);

// Longest string
Optional<String> longest = words.stream()
    .reduce((a, b) -> a.length() >= b.length() ? a : b);
```

### count()

```java
long premiumCount = orders.stream()
    .filter(Order::isPremium)
    .count();
```

### findFirst() / findAny()

```java
// Returns Optional<T> — always handle the empty case
Optional<User> first = users.stream()
    .filter(u -> u.getCity().equals("Bengaluru"))
    .findFirst();

first.ifPresent(u -> System.out.println(u.getName()));
String name = first.orElse(new User("Unknown")).getName();
```

### anyMatch / allMatch / noneMatch

```java
boolean hasOverdue   = orders.stream().anyMatch(o -> o.isOverdue());
boolean allDelivered = orders.stream().allMatch(o -> o.getStatus().equals("DELIVERED"));
boolean noneVoided   = orders.stream().noneMatch(o -> o.isVoided());
```

### min() / max()

```java
Optional<Order> highestOrder = orders.stream()
    .max(Comparator.comparingDouble(Order::getAmount));

Optional<String> shortest = names.stream()
    .min(Comparator.comparingInt(String::length));
```

### toArray()

```java
String[] namesArray = names.stream().toArray(String[]::new);
```

---

## 6. The Collectors Toolbox

`Collectors` is where streams become truly powerful. Import once:

```java
import java.util.stream.Collectors;
```

### Grouping

```java
// Group users by city → Map<String, List<User>>
Map<String, List<User>> byCity = users.stream()
    .collect(Collectors.groupingBy(User::getCity));

// Group and count → Map<String, Long>
Map<String, Long> countByCity = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.counting()
    ));

// Group and sum → Map<String, Double>
Map<String, Double> revenueByCategory = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getCategory,
        Collectors.summingDouble(Order::getAmount)
    ));

// Group and get average → Map<String, Double>
Map<String, Double> avgByCategory = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getCategory,
        Collectors.averagingDouble(Order::getAmount)
    ));

// Multi-level grouping → Map<String, Map<String, List<Order>>>
Map<String, Map<String, List<Order>>> grouped = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getCategory,
        Collectors.groupingBy(Order::getStatus)
    ));
```

### Joining strings

```java
// Simple join
String csv = names.stream().collect(Collectors.joining(", "));
// → "Alice, Bob, Charlie"

// With prefix and suffix
String formatted = names.stream()
    .collect(Collectors.joining(", ", "[", "]"));
// → "[Alice, Bob, Charlie]"
```

### toMap()

```java
// List<User> → Map<id, User>
Map<Integer, User> usersById = users.stream()
    .collect(Collectors.toMap(User::getId, u -> u));

// List<User> → Map<id, name>
Map<Integer, String> idToName = users.stream()
    .collect(Collectors.toMap(User::getId, User::getName));

// Handle duplicate keys with merge function
Map<String, Double> totalByCustomer = orders.stream()
    .collect(Collectors.toMap(
        Order::getCustomer,
        Order::getAmount,
        Double::sum          // merge: add amounts if key already exists
    ));
```

### partitioningBy() — split into two groups

```java
// → Map<Boolean, List<Order>>
Map<Boolean, List<Order>> partitioned = orders.stream()
    .collect(Collectors.partitioningBy(Order::isPremium));

List<Order> premiumOrders    = partitioned.get(true);
List<Order> nonPremiumOrders = partitioned.get(false);
```

### Statistics

```java
IntSummaryStatistics stats = orders.stream()
    .mapToInt(Order::getQuantity)
    .summaryStatistics();

stats.getSum();
stats.getAverage();
stats.getMax();
stats.getMin();
stats.getCount();
```

---

## 7. Real-World Examples — Order System

### The data model

```java
class Order {
    String  id;
    String  customer;
    String  category;
    String  status;       // "DELIVERED", "PENDING", "CANCELLED"
    double  amount;
    int     quantity;
    boolean isPremium;
}
```

### Scenario 1 — Total revenue from delivered orders

```java
double revenue = orders.stream()
    .filter(o -> o.getStatus().equals("DELIVERED"))
    .mapToDouble(Order::getAmount)
    .sum();
```

### Scenario 2 — Top 3 customers by total spend

```java
// Step 1: group and sum per customer
Map<String, Double> spendMap = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getCustomer,
        Collectors.summingDouble(Order::getAmount)
    ));

// Step 2: sort and take top 3
List<Map.Entry<String, Double>> top3 = spendMap.entrySet().stream()
    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    .limit(3)
    .collect(Collectors.toList());

top3.forEach(e -> System.out.println(e.getKey() + " → ₹" + e.getValue()));
```

### Scenario 3 — Count orders by status

```java
Map<String, Long> byStatus = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getStatus,
        Collectors.counting()
    ));
// → {"DELIVERED": 6, "PENDING": 2, "CANCELLED": 2}
```

### Scenario 4 — Premium orders, formatted as report string

```java
String report = orders.stream()
    .filter(Order::isPremium)
    .sorted(Comparator.comparingDouble(Order::getAmount).reversed())
    .map(o -> o.getId() + " | " + o.getCustomer() + " | ₹" + o.getAmount())
    .collect(Collectors.joining("\n"));
```

### Scenario 5 — Average order value per category

```java
Map<String, Double> avgByCategory = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getCategory,
        Collectors.averagingDouble(Order::getAmount)
    ));
```

### Scenario 6 — IDs of all items in pending orders (flatMap)

```java
List<String> pendingItemIds = orders.stream()
    .filter(o -> o.getStatus().equals("PENDING"))
    .flatMap(o -> o.getItems().stream())
    .map(Item::getId)
    .distinct()
    .collect(Collectors.toList());
```

### Scenario 7 — Check if any premium order exceeds ₹10,000

```java
boolean hasHighValue = orders.stream()
    .filter(Order::isPremium)
    .anyMatch(o -> o.getAmount() > 10_000);
```

### Scenario 8 — Order lookup map

```java
Map<String, Order> orderById = orders.stream()
    .collect(Collectors.toMap(Order::getId, o -> o));

Order order = orderById.get("ORD-001");
```

---

## 8. Common Patterns & Recipes

### Filter → Map → Collect (the bread and butter)

```java
List<String> result = list.stream()
    .filter(predicate)
    .map(transformer)
    .collect(Collectors.toList());
```

> Always filter **before** map. Filtering early means fewer elements to transform.

### Optional handling with findFirst

```java
// Never call .get() directly — it throws if empty
String name = users.stream()
    .filter(u -> u.getId() == targetId)
    .findFirst()
    .map(User::getName)
    .orElse("Unknown");
```

### Convert List to Map

```java
Map<Integer, User> map = users.stream()
    .collect(Collectors.toMap(User::getId, u -> u));
```

### Deduplication with a custom key

```java
// Keep only the first occurrence per email
List<User> deduped = users.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toMap(User::getEmail, u -> u, (a, b) -> a),
        m -> new ArrayList<>(m.values())
    ));
```

### Counting with a condition

```java
long count = list.stream().filter(condition).count();
```

### Getting max/min safely

```java
double maxAmount = orders.stream()
    .mapToDouble(Order::getAmount)
    .max()
    .orElse(0.0);
```

### Parallel streams (use carefully)

```java
// For large collections with CPU-bound operations
long count = bigList.parallelStream()
    .filter(expensiveCheck)
    .count();

// Avoid parallel for: ordered operations, I/O, small lists, stateful lambdas
```

---

## 9. Pitfalls to Avoid

| Pitfall | Wrong | Right |
|---|---|---|
| Reusing a stream | `stream.filter(...); stream.map(...)` | Create a new stream each time |
| Ignoring Optional | `findFirst().get()` | `.orElse()` or `.ifPresent()` |
| Side effects in map() | `map(u -> { db.save(u); return u; })` | Use `forEach()` for side effects |
| Sorting before filtering | `.sorted().filter()` | `.filter().sorted()` — filter first! |
| Primitive boxing overhead | `.map(i -> i * 2)` on integers | `.mapToInt(i -> i * 2)` |
| Modifying source inside stream | Mutating the original list | Collect to a new list |
| NullPointerException in map() | `.map(u -> u.getAddress().getCity())` | Add null checks or use `Optional` |

---

## 10. Interview Cheat Sheet

### Quick-fire Q&A

**Q: What is a Stream?**
A sequence of elements that supports sequential and parallel aggregate operations. It is not a data structure — it does not store data.

**Q: Intermediate vs Terminal?**
Intermediate operations return a Stream and are lazy (nothing runs). Terminal operations trigger execution and return a non-stream result (List, Map, int, void, etc.).

**Q: What does "lazy evaluation" mean?**
No computation happens until a terminal operation is called. The JVM chains all intermediate ops and executes them in a single pass.

**Q: map() vs flatMap()?**
`map()` is one-to-one — each element becomes one new element. `flatMap()` is one-to-many — each element becomes a stream, and all streams are merged (flattened) into one.

**Q: Can you reuse a Stream?**
No. Once a terminal operation is called, the stream is consumed. Calling any operation on it again throws `IllegalStateException`.

**Q: filter() vs map() — which first?**
Always `filter()` first. It reduces the number of elements so `map()` does less work.

**Q: What does collect(Collectors.groupingBy(...)) return?**
A `Map<K, List<V>>` by default, where K is the grouping key and the values are lists of matching elements.

**Q: What is the difference between findFirst() and findAny()?**
`findFirst()` always returns the first element in encounter order. `findAny()` may return any element — it is faster in parallel streams.

**Q: How is reduce() different from collect()?**
`reduce()` combines elements into a single immutable value (sum, product). `collect()` gathers elements into a mutable container (List, Map, String).

### The 5 most-used Collectors

```java
Collectors.toList()
Collectors.toSet()
Collectors.joining(", ")
Collectors.groupingBy(keyFn)
Collectors.groupingBy(keyFn, Collectors.counting())
```

### Mental checklist for any stream task

1. What is my **source**? (List, Set, Map entries, array?)
2. Do I need to **filter** first? (Almost always yes — do it early)
3. Am I **transforming** shape (`map`) or **grouping** (`collect + groupingBy`)?
4. What does my **terminal operation** return — a List, a Map, a single value, or a String?
5. Could any element be **null** or the result **empty**? Handle with Optional.

---

## Quick Reference Card

```
SOURCE          INTERMEDIATE (lazy, chainable)     TERMINAL (triggers execution)
──────────      ──────────────────────────────     ─────────────────────────────
.stream()       .filter(predicate)                 .collect(Collectors.toList())
.parallelStream .map(function)                     .collect(Collectors.toSet())
Stream.of(...)  .flatMap(function)                 .collect(Collectors.joining())
Arrays.stream() .sorted()                          .collect(Collectors.groupingBy())
IntStream.range .sorted(comparator)                .forEach(consumer)
                .distinct()                        .reduce(identity, accumulator)
                .limit(n)                          .count()
                .skip(n)                           .findFirst()  → Optional
                .peek(consumer)                    .findAny()    → Optional
                .mapToInt/Double/Long              .anyMatch(predicate)
                                                   .allMatch(predicate)
                                                   .noneMatch(predicate)
                                                   .min/max(comparator) → Optional
                                                   .toArray()
                                                   .sum() / .average()  (primitive streams)
```

---

*Built from hands-on learning — Java Streams, interactive examples, and real Order object scenarios.*
