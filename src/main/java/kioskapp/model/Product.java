package kioskapp.model;

import java.util.Objects;


 //One menu item in the kiosk.
public class Product {
    private String id;      // Unique ID (e.g., "C1")
    private String name;    // Product name (e.g., "Burger Meal")
    private double price;   // Product price


     //Creates a new product.
     //@param id    Product ID.
     //@param name  Product name.
     //@param price Product price.
    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }


     //Shows product info as a string.
     //@return Text like "C1 - Burger (₱99.00)"
    @Override
    public String toString() {
        return id + " - " + name + " (₱" + String.format("%.2f", price) + ")";
    }


     //Checks if two products are the same by ID.
     //@param o Another object.
     //@return True if IDs match.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }


     //Generates a hash code based on ID.
     //@return The hash code.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}