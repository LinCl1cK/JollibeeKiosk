package kioskapp.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kioskapp.manager.OrderManager;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;

import java.util.Locale;

/**
 * Controller for the Queue Display View.
 * This separate window shows orders that have been confirmed by the cashier
 * and are now in the "preparation" phase, waiting to be served.
 */
public class QueueDisplayController {
    private OrderManager orderManager;
    private TableView<Order> queueTable;

    /**
     * Constructs a QueueDisplayController.
     *
     * @param orderManager The manager for orders, from which to get the preparation queue.
     */
    public QueueDisplayController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    /**
     * Creates and returns the Scene for the Queue Display.
     *
     * @return The Scene object for the queue display interface.
     */
    public Scene getQueueDisplayScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #e8f5e9;"); // Light green background

        Label title = new Label("Order Preparation Queue");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #388e3c;"); // Darker green

        queueTable = new TableView<>();
        queueTable.setPrefHeight(400);
        queueTable.setItems(orderManager.getPreparingOrders()); // Bind to the observable list

        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(100);

        TableColumn<Order, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isPriority() ? "YES" : "NO"));
        priorityCol.setPrefWidth(80);

        TableColumn<Order, String> orderTimeCol = new TableColumn<>("Time Placed");
        orderTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        ));
        orderTimeCol.setPrefWidth(120);

        TableColumn<Order, Double> totalCostCol = new TableColumn<>("Total (₱)");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        totalCostCol.setPrefWidth(100);
        totalCostCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "%.2f", price));
                }
            }
        });

        TableColumn<Order, String> itemsSummaryCol = new TableColumn<>("Items");
        itemsSummaryCol.setCellValueFactory(cellData -> {
            StringBuilder sb = new StringBuilder();
            for (OrderItem item : cellData.getValue().getItems()) {
                sb.append(item.getProduct().getName()).append(" (x").append(item.getQuantity()).append("), ");
            }
            return new SimpleStringProperty(sb.toString().replaceAll(", $", "")); // Remove trailing comma and space
        });
        itemsSummaryCol.setPrefWidth(300);

        queueTable.getColumns().addAll(orderIdCol, priorityCol, orderTimeCol, totalCostCol, itemsSummaryCol);
        queueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Button to simulate completing an order from the queue (e.g., kitchen marks as ready)
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button markAsReadyButton = new Button("Mark Selected Order as Ready");
        markAsReadyButton.setStyle("-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
        markAsReadyButton.setOnMouseEntered(e -> markAsReadyButton.setStyle("-fx-background-color: #0097a7; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));
        markAsReadyButton.setOnMouseExited(e -> markAsReadyButton.setStyle("-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));

        markAsReadyButton.setOnAction(e -> markSelectedOrderAsReady());

        Button backButton = new Button("Close Display");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        backButton.setOnAction(e -> ((Stage)root.getScene().getWindow()).close()); // Close this window

        buttonBox.getChildren().addAll(markAsReadyButton, backButton);


        root.getChildren().addAll(title, queueTable, buttonBox);

        return new Scene(root, 900, 600); // Larger scene for queue display
    }

    /**
     * Marks the selected order in the table as ready, removing it from the preparation queue.
     * This simulates the kitchen completing an order.
     */
    private void markSelectedOrderAsReady() {
        Order selectedOrder = queueTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "No Order Selected", "Please select an order to mark as ready.");
            return;
        }

        if (orderManager.completePreparation(selectedOrder.getOrderId())) {
            showAlert(Alert.AlertType.INFORMATION, "Order Ready!", "Order #" + selectedOrder.getOrderId() + " is now ready for pickup!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not mark order #" + selectedOrder.getOrderId() + " as ready. It might have already been removed.");
        }
    }

    /**
     * Displays an alert dialog.
     *
     * @param type    The type of alert (e.g., WARNING, INFORMATION).
     * @param title   The title of the alert dialog.
     * @param message The message content of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}