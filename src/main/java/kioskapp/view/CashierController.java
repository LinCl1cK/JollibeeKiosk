package kioskapp.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kioskapp.manager.OrderManager;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;

import java.util.Locale;

/**
 * Controller for the Cashier View.
 * Allows cashiers to retrieve the next pending order, review its details,
 * confirm it for payment, and send it to the kitchen preparation queue.
 */
public class CashierController {
    private OrderManager orderManager;
    private Order currentProcessingOrder; // The order currently being processed by the cashier

    // UI Elements
    private Label orderIdLabel;
    private Label orderTimeLabel;
    private Label orderPriorityLabel; // New label for priority status
    private Label orderTotalLabel;
    private TableView<OrderItem> orderItemsTable;
    private Label statusLabel;

    /**
     * Constructs a CashierController.
     *
     * @param orderManager The manager for orders.
     */
    public CashierController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    /**
     * Creates and returns the Scene for the Cashier View.
     *
     * @return The Scene object for the cashier interface.
     */
    public Scene getCashierScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fff9c4;"); // Light yellow background

        Label title = new Label("Cashier Operations");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #e62429;"); // Jollibee red

        GridPane orderDetailsGrid = new GridPane();
        orderDetailsGrid.setHgap(10);
        orderDetailsGrid.setVgap(10);
        orderDetailsGrid.setPadding(new Insets(10));
        orderDetailsGrid.setStyle("-fx-border-color: #ffca28; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-padding: 10;");

        orderIdLabel = new Label("Order ID: N/A");
        orderTimeLabel = new Label("Time Placed: N/A");
        orderPriorityLabel = new Label("Priority: N/A");
        orderTotalLabel = new Label("Total: ₱0.00");

        String detailStyle = "-fx-font-size: 16px; -fx-font-weight: bold;";
        orderIdLabel.setStyle(detailStyle);
        orderTimeLabel.setStyle(detailStyle);
        orderPriorityLabel.setStyle(detailStyle);
        orderTotalLabel.setStyle(detailStyle + "-fx-text-fill: #e62429;");

        orderDetailsGrid.add(new Label("Order ID:"), 0, 0);
        orderDetailsGrid.add(orderIdLabel, 1, 0);
        orderDetailsGrid.add(new Label("Time Placed:"), 0, 1);
        orderDetailsGrid.add(orderTimeLabel, 1, 1);
        orderDetailsGrid.add(new Label("Priority Status:"), 0, 2);
        orderDetailsGrid.add(orderPriorityLabel, 1, 2);
        orderDetailsGrid.add(new Label("Order Total:"), 0, 3);
        orderDetailsGrid.add(orderTotalLabel, 1, 3);

        Label itemsLabel = new Label("Order Items:");
        itemsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e62429; -fx-padding: 10 0 0 0;");

        orderItemsTable = new TableView<>();
        orderItemsTable.setPrefHeight(200);

        TableColumn<OrderItem, String> itemNameCol = new TableColumn<>("Item");
        itemNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<OrderItem, Integer> itemQtyCol = new TableColumn<>("Qty");
        itemQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> itemPriceCol = new TableColumn<>("Subtotal (₱)");
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        itemPriceCol.setCellFactory(tc -> new javafx.scene.control.TableCell<OrderItem, Double>() {
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

        orderItemsTable.getColumns().addAll(itemNameCol, itemQtyCol, itemPriceCol);
        orderItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        Button retrieveButton = new Button("Retrieve Next Order");
        Button confirmButton = new Button("Confirm & Print Receipt");
        Button backButton = new Button("Back to Main Menu"); // Placeholder for closing window

        String buttonStyle = "-fx-background-color: #f7a400; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);";
        String confirmButtonStyle = "-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);";
        String backButtonStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;";

        retrieveButton.setStyle(buttonStyle);
        confirmButton.setStyle(confirmButtonStyle);
        backButton.setStyle(backButtonStyle);

        retrieveButton.setOnMouseEntered(e -> retrieveButton.setStyle("-fx-background-color: #e09500; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));
        retrieveButton.setOnMouseExited(e -> retrieveButton.setStyle(buttonStyle));
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(confirmButtonStyle));
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(backButtonStyle));


        retrieveButton.setOnAction(e -> retrieveNextOrder());
        confirmButton.setOnAction(e -> confirmOrder());
        backButton.setOnAction(e -> ((Stage)root.getScene().getWindow()).close()); // Close this window

        buttons.getChildren().addAll(retrieveButton, confirmButton);

        statusLabel = new Label("No order retrieved.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #666;");

        root.getChildren().addAll(title, orderDetailsGrid, itemsLabel, orderItemsTable, buttons, statusLabel, backButton);

        return new Scene(root, 650, 700);
    }

    /**
     * Retrieves the next available order from the queue and displays its details.
     */
    private void retrieveNextOrder() {
        currentProcessingOrder = orderManager.retrieveNextOrder();
        if (currentProcessingOrder != null) {
            orderIdLabel.setText("Order ID: " + currentProcessingOrder.getOrderId());
            orderTimeLabel.setText("Time Placed: " + currentProcessingOrder.getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            orderPriorityLabel.setText("Priority: " + (currentProcessingOrder.isPriority() ? "YES" : "NO"));
            orderTotalLabel.setText("Total: ₱" + String.format(Locale.US, "%.2f", currentProcessingOrder.getTotalCost()));
            orderItemsTable.setItems(FXCollections.observableArrayList(currentProcessingOrder.getItems()));
            statusLabel.setText("Order #" + currentProcessingOrder.getOrderId() + " retrieved. Verify and Confirm.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Orders", "No pending orders in the queue.");
            clearOrderDisplay();
        }
    }

    /**
     * Confirms the currently retrieved order, sends it to preparation, and clears the display.
     */
    private void confirmOrder() {
        if (currentProcessingOrder == null) {
            showAlert(Alert.AlertType.WARNING, "No Order to Confirm", "Please retrieve an order first.");
            return;
        }

        orderManager.sendOrderToPreparation(currentProcessingOrder); // Send to kitchen queue
        showAlert(Alert.AlertType.INFORMATION, "Order Confirmed", "Order #" + currentProcessingOrder.getOrderId() + " confirmed and sent to kitchen for preparation.");
        clearOrderDisplay();
        currentProcessingOrder = null; // Clear the reference
    }

    /**
     * Clears all order details from the display.
     */
    private void clearOrderDisplay() {
        orderIdLabel.setText("Order ID: N/A");
        orderTimeLabel.setText("Time Placed: N/A");
        orderPriorityLabel.setText("Priority: N/A");
        orderTotalLabel.setText("Total: ₱0.00");
        orderItemsTable.setItems(FXCollections.emptyObservableList());
        statusLabel.setText("Ready for next order.");
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