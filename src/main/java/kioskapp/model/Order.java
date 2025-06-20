package kioskapp.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

/**
 * Represents a customer's order.
 */
public class Order {
    private String orderId; // Unique ID for the order
    private LocalDateTime orderTime; // Time the order was placed
    private List<OrderItem> items; // List of items in the order
    private boolean isPriority; // If the order is for priority customer


     //Creates a new order.
     //@param orderId    The order's unique ID.
     //@param isPriority True if it's a priority order.

    public Order(String orderId, boolean isPriority) {
        this.orderId = orderId;
        this.orderTime = LocalDateTime.now();
        this.items = new LinkedList<>();
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


     //Adds an item to the order.
     //If the item already exists, it adds the quantity.
     //@param item The item to add.

    public void addOrderItem(OrderItem item) {
        for (OrderItem existingItem : items) {
            if (existingItem.getProduct().getId().equals(item.getProduct().getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }


     //Calculates the total price of all items.
     //@return The total cost.

    public double getTotalCost() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }


     //Returns a simple string showing order info.
     //@return A formatted string.

    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        return "Order #" + orderId + " (" + (isPriority ? "PRIORITY" : "NORMAL") + ") - "
                + currencyFormat.format(getTotalCost()) + " at "
                + orderTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}