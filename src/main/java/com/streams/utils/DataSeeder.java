package com.streams.utils;

import com.streams.model.Item;
import com.streams.model.Order;

import java.time.LocalDate;
import java.util.List;

/**
 * Provides a fixed set of 20 sample orders for all stream demos.
 * Covers multiple customers, categories, statuses, cities, and amounts.
 */
public class DataSeeder {

    public static List<Order> getOrders() {
        return List.of(

            new Order("ORD-001", "Priya",   "Electronics", "DELIVERED", 12500, 1, true,
                LocalDate.of(2024, 1, 5),  "Bengaluru",
                List.of(new Item("Noise Cancelling Headphones", "ELEC-001", 12500, 1))),

            new Order("ORD-002", "Rahul",   "Clothing",    "PENDING",    850,  3, false,
                LocalDate.of(2024, 1, 10), "Mumbai",
                List.of(new Item("Cotton T-Shirt", "CLO-001", 450, 2),
                        new Item("Socks Pack",     "CLO-002", 200, 1))),

            new Order("ORD-003", "Priya",   "Books",       "DELIVERED",  450,  5, true,
                LocalDate.of(2024, 1, 12), "Bengaluru",
                List.of(new Item("Clean Code",             "BOOK-001", 250, 1),
                        new Item("Effective Java",         "BOOK-002", 200, 1))),

            new Order("ORD-004", "Anita",   "Electronics", "CANCELLED", 3200,  1, false,
                LocalDate.of(2024, 1, 15), "Delhi",
                List.of(new Item("Smartwatch", "ELEC-002", 3200, 1))),

            new Order("ORD-005", "Rahul",   "Electronics", "DELIVERED", 7800,  2, false,
                LocalDate.of(2024, 2, 1),  "Mumbai",
                List.of(new Item("Mechanical Keyboard", "ELEC-003", 3900, 2))),

            new Order("ORD-006", "Arjun",   "Clothing",    "DELIVERED", 1200,  4, true,
                LocalDate.of(2024, 2, 5),  "Chennai",
                List.of(new Item("Formal Shirt", "CLO-003", 600, 2),
                        new Item("Belt",         "CLO-004", 0,   0))),

            new Order("ORD-007", "Anita",   "Books",       "DELIVERED",  620,  2, false,
                LocalDate.of(2024, 2, 8),  "Delhi",
                List.of(new Item("Design Patterns", "BOOK-003", 320, 1),
                        new Item("Java Concurrency","BOOK-004", 300, 1))),

            new Order("ORD-008", "Arjun",   "Electronics", "PENDING",   9400,  1, true,
                LocalDate.of(2024, 2, 14), "Chennai",
                List.of(new Item("4K Monitor", "ELEC-004", 9400, 1))),

            new Order("ORD-009", "Priya",   "Clothing",    "DELIVERED", 2100,  6, true,
                LocalDate.of(2024, 2, 20), "Bengaluru",
                List.of(new Item("Linen Saree",  "CLO-005", 350, 6))),

            new Order("ORD-010", "Rahul",   "Books",       "CANCELLED",  300,  1, false,
                LocalDate.of(2024, 3, 1),  "Mumbai",
                List.of(new Item("Spring Boot in Action", "BOOK-005", 300, 1))),

            new Order("ORD-011", "Meena",   "Groceries",   "DELIVERED",  980,  8, false,
                LocalDate.of(2024, 3, 3),  "Hyderabad",
                List.of(new Item("Organic Rice",  "GRO-001", 120, 5),
                        new Item("Cold Pressed Oil","GRO-002", 340, 1))),

            new Order("ORD-012", "Meena",   "Electronics", "DELIVERED", 5600,  1, true,
                LocalDate.of(2024, 3, 7),  "Hyderabad",
                List.of(new Item("Tablet",        "ELEC-005", 5600, 1))),

            new Order("ORD-013", "Arjun",   "Groceries",   "PENDING",    450,  4, false,
                LocalDate.of(2024, 3, 10), "Chennai",
                List.of(new Item("Green Tea",     "GRO-003", 110, 4))),

            new Order("ORD-014", "Anita",   "Clothing",    "DELIVERED", 1850,  3, true,
                LocalDate.of(2024, 3, 15), "Delhi",
                List.of(new Item("Kurta Set",     "CLO-006", 620, 3))),

            new Order("ORD-015", "Meena",   "Books",       "DELIVERED",  890,  3, false,
                LocalDate.of(2024, 3, 18), "Hyderabad",
                List.of(new Item("DDIA",          "BOOK-006", 450, 1),
                        new Item("System Design", "BOOK-007", 440, 1))),

            new Order("ORD-016", "Priya",   "Groceries",   "CANCELLED",  275,  3, true,
                LocalDate.of(2024, 3, 20), "Bengaluru",
                List.of(new Item("Spice Mix",     "GRO-004",  90, 3))),

            new Order("ORD-017", "Rahul",   "Electronics", "DELIVERED", 18500, 1, true,
                LocalDate.of(2024, 4, 2),  "Mumbai",
                List.of(new Item("Gaming Laptop", "ELEC-006", 18500, 1))),

            new Order("ORD-018", "Anita",   "Groceries",   "DELIVERED",  640,  6, false,
                LocalDate.of(2024, 4, 5),  "Delhi",
                List.of(new Item("Pulses Bundle", "GRO-005", 640, 6))),

            new Order("ORD-019", "Arjun",   "Books",       "DELIVERED",  760,  2, true,
                LocalDate.of(2024, 4, 8),  "Chennai",
                List.of(new Item("Kubernetes Up & Running", "BOOK-008", 380, 2))),

            new Order("ORD-020", "Meena",   "Clothing",    "PENDING",   1300,  2, false,
                LocalDate.of(2024, 4, 12), "Hyderabad",
                List.of(new Item("Jeans",         "CLO-007", 650, 2)))
        );
    }
}
