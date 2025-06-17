package kioskapp.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages customer orders and the queuing system.
 * It uses a PriorityQueue for orders waiting for the cashier (prioritizing
 * elderly/PWD/pregnant customers) and an ObservableList for orders
 * currently being prepared in the kitchen.
 */
public class OrderManager {
    // Queue for orders waiting to be processed by the cashier
    private PriorityQueue<Order> pendingCashierQueue;
    // ObservableList for orders being prepared in the kitchen (for queue display)
    private ObservableList<Order> preparingOrdersObservable;
    // Unique counter for order IDs
    private static AtomicLong orderCounter = new AtomicLong(100);

    /**
     * Constructs a new OrderManager.
     * Initializes the priority queue with a custom comparator and the observable list.
     */
    public OrderManager() {
        // Custom comparator for the priority queue:
        // 1. Priority orders (isPriority = true) come first.
        // 2. Among priority or non-priority orders, earlier orders come first.
        // This lambda expression explicitly defines the comparison logic,
        // avoiding issues with Comparator.comparingBoolean in some environments.
        pendingCashierQueue = new PriorityQueue<>(
                (o1, o2) -> {
                    // Compare by priority: true (priority) comes before false (normal).
                    // We use Boolean.compare(o2.isPriority(), o1.isPriority()) to put 'true' first.
                    int priorityCompare = Boolean.compare(o2.isPriority(), o1.isPriority());
                    if (priorityCompare != 0) {
                        return priorityCompare;
                    }
                    // If priorities are the same, compare by order time (earlier orders come first).
                    return o1.getOrderTime().compareTo(o2.getOrderTime());
                }
        );
        preparingOrdersObservable = FXCollections.observableArrayList();
    }

    /**
     * Places a new customer order into the pending cashier queue.
     * A unique order ID is generated for each new order.
     *
     * @param customerOrder The Order object to be placed.
     */
    public void placeOrder(Order customerOrder) {
        // Generate a simple sequential order ID for display purposes
        String newOrderId = String.valueOf(orderCounter.getAndIncrement());
        Order finalOrder = new Order(newOrderId, customerOrder.isPriority());
        // Copy items from the original order object provided by customer controller
        // This ensures the order ID and timestamp are set by the manager.
        for(OrderItem item : customerOrder.getItems()) {
            finalOrder.addOrderItem(item);
        }

        pendingCashierQueue.offer(finalOrder);
        // Note: The cashier controller will query the queue directly, no need for separate observable here.
        // The QueueDisplayController will observe preparingOrdersObservable.
        System.out.println("Order #" + finalOrder.getOrderId() + " placed. Priority: " + finalOrder.isPriority());
    }

    /**
     * Retrieves the next order for the cashier to process.
     * This order is removed from the pending queue.
     *
     * @return The next Order in the queue, or null if the queue is empty.
     */
    public Order retrieveNextOrder() {
        return pendingCashierQueue.poll(); // Retrieves and removes the head of the queue
    }

    /**
     * Sends a confirmed order to the preparation (kitchen) queue.
     * This order will now appear on the Queue Display screen.
     *
     * @param order The Order to send to preparation.
     */
    public void sendOrderToPreparation(Order order) {
        if (order != null) {
            preparingOrdersObservable.add(order);
            System.out.println("Order #" + order.getOrderId() + " moved to preparation.");
        }
    }

    /**
     * Marks an order as completed (served) and removes it from the preparation queue.
     *
     * @param orderId The ID of the order to mark as complete.
     * @return true if the order was found and removed, false otherwise.
     */
    public boolean completePreparation(String orderId) {
        boolean removed = preparingOrdersObservable.removeIf(order -> order.getOrderId().equals(orderId));
        if (removed) {
            System.out.println("Order #" + orderId + " completed and removed from preparation queue.");
        } else {
            System.out.println("Order #" + orderId + " not found in preparation queue.");
        }
        return removed;
    }

    /**
     * Returns an observable list of orders currently being prepared.
     * This list is intended for the Queue Display.
     *
     * @return An ObservableList of Order objects.
     */
    public ObservableList<Order> getPreparingOrders() {
        return preparingOrdersObservable;
    }

    /**
     * Returns the entire pending cashier queue.
     * This is primarily for the Cashier to view available orders without polling.
     *
     * @return A copy of the current pending cashier queue.
     */
    public ObservableList<Order> getPendingCashierQueueAsObservable() {
        // Convert the PriorityQueue to an ObservableList for display in Cashier view
        // This creates a snapshot. If the cashier view needs real-time updates without polling,
        // we would need an ObservablePriorityQueue or push updates.
        // For simplicity, for the cashier, we'll just poll or refresh the view when needed.
        return FXCollections.observableArrayList(pendingCashierQueue);
    }

    /**
     * Checks if the pending cashier queue is empty.
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isPendingCashierQueueEmpty() {
        return pendingCashierQueue.isEmpty();
    }
}
