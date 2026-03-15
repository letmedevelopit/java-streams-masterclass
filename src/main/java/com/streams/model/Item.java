package com.streams.model;

public class Item {

    private final String name;
    private final String sku;
    private final double price;
    private final int    quantity;

    public Item(String name, String sku, double price, int quantity) {
        this.name     = name;
        this.sku      = sku;
        this.price    = price;
        this.quantity = quantity;
    }

    public String getName()     { return name; }
    public String getSku()      { return sku; }
    public double getPrice()    { return price; }
    public int    getQuantity() { return quantity; }

    @Override
    public String toString() {
        return String.format("Item{name='%s', sku='%s', price=%.0f, qty=%d}", name, sku, price, quantity);
    }
}
