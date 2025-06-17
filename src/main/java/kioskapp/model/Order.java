package kioskapp.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Represents a complete customer order, including a list of order items,
 * the time the order was placed, and a unique order ID.
 * It also includes a flag to indicate if it's a priority order.
 */
public class Order {
    private String orderId;
    private LocalDateTime orderTime;
    private List<OrderItem> items;
    private boolean isPriority; // New field for priority

    /**
     * Constructs a new Order.
     *
     * @param orderId   The unique identifier for this order.
     * @param isPriority True if this is a priority order (elderly, PWD, pregnant), false otherwise.
     */
    public Order(String orderId, boolean isPriority) {
        this.orderId = orderId;
        this.orderTime = LocalDateTime.now(); // Timestamp when order is created
        this.items = new LinkedList<>(); // Use LinkedList for efficient additions/removals
        this.isPriority = isPriority;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isPriority() {
        return isPriority;
    }

    /**
     * Adds an OrderItem to the order.
     *
     * @param item The OrderItem to add.
     */
    public void addOrderItem(OrderItem item) {
        // Check if the product already exists in the order, if so, update quantity
        for (OrderItem existingItem : items) {
            if (existingItem.getProduct().getId().equals(item.getProduct().getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    /**
     * Removes an OrderItem from the order.
     *
     * @param item The OrderItem to remove.
     * @return true if the item was found and removed, false otherwise.
     */
    public boolean removeOrderItem(OrderItem item) {
        return items.remove(item);
    }

    /**
     * Calculates the total cost of all items in the order.
     *
     * @return The total price of the order.
     */
    public double getTotalCost() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    /**
     * Provides a string representation of the order, including its ID, time, and total cost.
     * @return Formatted string for the order.
     */
    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH")); // Philippines Peso
        return "Order #" + orderId + " (" + (isPriority ? "PRIORITY" : "NORMAL") + ") - " + currencyFormat.format(getTotalCost()) + " at " + orderTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}