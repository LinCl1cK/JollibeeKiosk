package kioskapp.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kioskapp.manager.OrderManager;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;

import java.util.Locale;


 //This controller manages the display for the queue of orders
 //that are currently being prepared in the kitchen.

public class QueueDisplayController {
    private OrderManager orderManager;
    private TableView<Order> queueTable;


     //Constructor receives the OrderManager, which manages all orders.
    public QueueDisplayController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }


    //Creates the user interface for the queue display screen.
    public Scene getQueueDisplayScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #e8f5e9;"); // light green background

        // Page title
        Label title = new Label("Order Preparation Queue");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #388e3c;");

        // Table setup
        queueTable = new TableView<>();
        queueTable.setPrefHeight(400);
        queueTable.setItems(orderManager.getPreparingOrders()); // Binds the list of preparing orders

        // Column for order ID
        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(100);

        // Column for priority status
        TableColumn<Order, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPriority() ? "YES" : "NO"));
        priorityCol.setPrefWidth(80);

        // Column for order time
        TableColumn<Order, String> orderTimeCol = new TableColumn<>("Time Placed");
        orderTimeCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        ));
        orderTimeCol.setPrefWidth(120);

        // Column for total cost
        TableColumn<Order, Double> totalCostCol = new TableColumn<>("Total (₱)");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        totalCostCol.setPrefWidth(100);
        totalCostCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format(Locale.US, "%.2f", price));
            }
        });

        // Column for listing item summary
        TableColumn<Order, String> itemsSummaryCol = new TableColumn<>("Items");
        itemsSummaryCol.setCellValueFactory(data -> {
            StringBuilder sb = new StringBuilder();
            for (OrderItem item : data.getValue().getItems()) {
                sb.append(item.getProduct().getName()).append(" (x").append(item.getQuantity()).append("), ");
            }
            return new SimpleStringProperty(sb.toString().replaceAll(", $", ""));
        });
        itemsSummaryCol.setPrefWidth(300);

        // Add all columns to the table
        queueTable.getColumns().addAll(orderIdCol, priorityCol, orderTimeCol, totalCostCol, itemsSummaryCol);
        queueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Button: Mark an order as ready (simulate kitchen completion)
        Button markAsReadyButton = new Button("Mark Selected Order as Ready");
        markAsReadyButton.setStyle("-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-weight: bold;");
        markAsReadyButton.setOnMouseEntered(e -> markAsReadyButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold;"));
        markAsReadyButton.setOnMouseExited(e -> markAsReadyButton.setStyle("-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-weight: bold;"));
        markAsReadyButton.setOnAction(e -> markSelectedOrderAsReady());

        // Button: Close the queue display
        Button backButton = new Button("Close Display");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;"));
        backButton.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        buttonBox.getChildren().addAll(markAsReadyButton, backButton);

        // Add all components to root layout
        root.getChildren().addAll(title, queueTable, buttonBox);

        return new Scene(root, 900, 600); // Set scene size
    }


    //Handles the event when the kitchen marks an order as completed.
    private void markSelectedOrderAsReady() {
        Order selectedOrder = queueTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "No Order Selected", "Please select an order to mark as ready.");
            return;
        }

        boolean success = orderManager.completePreparation(selectedOrder.getOrderId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Order Ready!", "Order #" + selectedOrder.getOrderId() + " is now ready for pickup!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to mark the order as ready. It might already be completed.");
        }
    }


    //Utility method to show alert dialogs.
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}