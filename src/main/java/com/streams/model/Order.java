package com.streams.model;

import java.time.LocalDate;
import java.util.List;

public class Order {

    private final String      id;
    private final String      customer;
    private final String      category;
    private final String      status;
    private final double      amount;
    private final int         quantity;
    private final boolean     isPremium;
    private final LocalDate   orderDate;
    private final String      city;
    private final List<Item>  items;

    public Order(String id, String customer, String category, String status,
                 double amount, int quantity, boolean isPremium,
                 LocalDate orderDate, String city, List<Item> items) {
        this.id        = id;
        this.customer  = customer;
        this.category  = category;
        this.status    = status;
        this.amount    = amount;
        this.quantity  = quantity;
        this.isPremium = isPremium;
        this.orderDate = orderDate;
        this.city      = city;
        this.items     = items;
    }

    // --- Getters ---
    public String    getId()        { return id; }
    public String    getCustomer()  { return customer; }
    public String    getCategory()  { return category; }
    public String    getStatus()    { return status; }
    public double    getAmount()    { return amount; }
    public int       getQuantity()  { return quantity; }
    public boolean   isPremium()    { return isPremium; }
    public LocalDate getOrderDate() { return orderDate; }
    public String    getCity()      { return city; }
    public List<Item> getItems()    { return items; }

    @Override
    public String toString() {
        return String.format("Order{id='%s', customer='%s', category='%s', status='%s', amount=%.0f, premium=%s}",
                id, customer, category, status, amount, isPremium);
    }
}
