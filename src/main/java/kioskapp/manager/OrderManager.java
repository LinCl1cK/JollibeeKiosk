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


 //Handles customer orders and manages the order queues.
public class OrderManager {
    // Queue for orders waiting for the cashier
    private PriorityQueue<Order> pendingCashierQueue;
    // List of orders currently being prepared
    private ObservableList<Order> preparingOrdersObservable;
    // Counter to generate unique order IDs
    private static AtomicLong orderCounter = new AtomicLong(100);


    //Creates a new order manager and sets up the queues.

    public OrderManager() {
        // Priority orders go first; earlier orders are ahead if priority is the same
        pendingCashierQueue = new PriorityQueue<>(
                (o1, o2) -> {
                    int priorityCompare = Boolean.compare(o2.isPriority(), o1.isPriority());
                    if (priorityCompare != 0) {
                        return priorityCompare;
                    }
                    return o1.getOrderTime().compareTo(o2.getOrderTime());
                }
        );
        preparingOrdersObservable = FXCollections.observableArrayList();
    }


     //Adds a new customer order to the pending queue.
     //@param customerOrder The order to be placed.

    public void placeOrder(Order customerOrder) {
        // Create a unique order ID
        String newOrderId = String.valueOf(orderCounter.getAndIncrement());
        Order finalOrder = new Order(newOrderId, customerOrder.isPriority());

        // Copy all items into the new order
        for(OrderItem item : customerOrder.getItems()) {
            finalOrder.addOrderItem(item);
        }
        pendingCashierQueue.offer(finalOrder);
        System.out.println("Order #" + finalOrder.getOrderId() + " placed. Priority: " + finalOrder.isPriority());
    }

     //Gets the next order from the pending queue.
     //@return The next order or null if empty.
    public Order retrieveNextOrder() {
        return pendingCashierQueue.poll();
    }


     //Sends an order to the kitchen for preparation.
     //@param order The order to prepare.

    public void sendOrderToPreparation(Order order) {
        if (order != null) {
            preparingOrdersObservable.add(order);
            System.out.println("Order #" + order.getOrderId() + " moved to preparation.");
        }
    }


     //Marks an order as done and removes it from the preparation list.
     //@param orderId The ID of the completed order.
     //@return true if removed, false if not found.

    public boolean completePreparation(String orderId) {
        boolean removed = preparingOrdersObservable.removeIf(order -> order.getOrderId().equals(orderId));
        if (removed) {
            System.out.println("Order #" + orderId + " completed and removed from preparation queue.");
        } else {
            System.out.println("Order #" + orderId + " not found in preparation queue.");
        }
        return removed;
    }


      //Gets the list of orders currently in preparation.
      //@return Observable list of preparing orders.
    public ObservableList<Order> getPreparingOrders() {
        return preparingOrdersObservable;
    }


      //Gets a snapshot of the pending cashier queue.
     //@return List of pending orders.

    public ObservableList<Order> getPendingCashierQueueAsObservable() {
        return FXCollections.observableArrayList(pendingCashierQueue);
    }


      //Checks if there are no orders waiting for the cashier.
     //@return true if no pending orders.

    public boolean isPendingCashierQueueEmpty() {
        return pendingCashierQueue.isEmpty();
    }
}