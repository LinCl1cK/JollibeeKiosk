package kioskapp.model;

import java.util.Objects;

/**
 * Represents a single menu item available in the kiosk.
 * Each product has a unique ID, a name, and a price.
 */
public class Product {
    private String id;
    private String name;
    private double price;

    /**
     * Constructs a new Product.
     *
     * @param id    The unique identifier for the product (e.g., "C1", "S1").
     * @param name  The name of the product (e.g., "Chickenjoy 1pc Meal").
     * @param price The price of the product.
     */
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

    // Setters (if product details can be updated)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Overrides the toString method to provide a meaningful string representation of the Product.
     *
     * @return A string containing the product ID, name, and price.
     */
    @Override
    public String toString() {
        return id + " - " + name + " (₱" + String.format("%.2f", price) + ")";
    }

    /**
     * Overrides the equals method to compare Product objects based on their ID.
     *
     * @param o The object to compare with.
     * @return true if the products have the same ID, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    /**
     * Overrides the hashCode method, consistent with equals.
     *
     * @return The hash code for the product.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}