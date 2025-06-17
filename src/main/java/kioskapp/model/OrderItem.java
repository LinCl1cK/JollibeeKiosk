package kioskapp.model;

/**
 * Represents a single item within a customer's order, including the product and quantity.
 */
public class OrderItem {
    private Product product;
    private int quantity;

    /**
     * Constructs a new OrderItem.
     *
     * @param product  The product being ordered.
     * @param quantity The quantity of the product.
     */
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculates the total price for this order item (product price * quantity).
     *
     * @return The total price for the order item.
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    /**
     * Overrides toString for meaningful representation.
     * @return String format of the order item.
     */
    @Override
    public String toString() {
        return product.getName() + " x" + quantity + " (₱" + String.format("%.2f", getTotalPrice()) + ")";
    }
}