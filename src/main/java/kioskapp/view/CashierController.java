package kioskapp.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import kioskapp.manager.OrderManager;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;

import java.util.Locale;


 //Handles the Cashier View.
 //Allows the cashier to get the next order, view details, confirm payment,
 //and send it to the kitchen.

public class CashierController {
    private OrderManager orderManager;
    private Order currentProcessingOrder; // The order currently being handled

    // UI Components
    private Label orderIdLabel;
    private Label orderTimeLabel;
    private Label orderPriorityLabel;
    private Label orderTotalLabel;
    private TableView<OrderItem> orderItemsTable;
    private Label statusLabel;


    //Constructor that receives an order manager.

    public CashierController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }


     //Builds and returns the UI for the cashier screen.
    public Scene getCashierScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fff9c4;"); // Yellow background

        // Header title
        Label title = new Label("Cashier Operations");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #e62429;");

        // Order details section
        GridPane orderDetailsGrid = new GridPane();
        orderDetailsGrid.setHgap(10);
        orderDetailsGrid.setVgap(10);
        orderDetailsGrid.setPadding(new Insets(10));
        orderDetailsGrid.setStyle("-fx-border-color: #ffca28; -fx-border-width: 2px; -fx-border-radius: 8px;");

        // Order info labels
        orderIdLabel = new Label("Order ID: N/A");
        orderTimeLabel = new Label("Time Placed: N/A");
        orderPriorityLabel = new Label("Priority: N/A");
        orderTotalLabel = new Label("Total: ₱0.00");

        // Label style
        String detailStyle = "-fx-font-size: 16px; -fx-font-weight: bold;";
        orderIdLabel.setStyle(detailStyle);
        orderTimeLabel.setStyle(detailStyle);
        orderPriorityLabel.setStyle(detailStyle);
        orderTotalLabel.setStyle(detailStyle + "-fx-text-fill: #e62429;");

        // Add to grid
        orderDetailsGrid.add(new Label("Order ID:"), 0, 0);
        orderDetailsGrid.add(orderIdLabel, 1, 0);
        orderDetailsGrid.add(new Label("Time Placed:"), 0, 1);
        orderDetailsGrid.add(orderTimeLabel, 1, 1);
        orderDetailsGrid.add(new Label("Priority Status:"), 0, 2);
        orderDetailsGrid.add(orderPriorityLabel, 1, 2);
        orderDetailsGrid.add(new Label("Order Total:"), 0, 3);
        orderDetailsGrid.add(orderTotalLabel, 1, 3);

        // Table for items in the order
        Label itemsLabel = new Label("Order Items:");
        itemsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e62429;");

        orderItemsTable = new TableView<>();
        orderItemsTable.setPrefHeight(200);

        // Table columns
        TableColumn<OrderItem, String> itemNameCol = new TableColumn<>("Item");
        itemNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProduct().getName()));

        TableColumn<OrderItem, Integer> itemQtyCol = new TableColumn<>("Qty");
        itemQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> itemPriceCol = new TableColumn<>("Subtotal (₱)");
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        itemPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format(Locale.US, "%.2f", price));
            }
        });

        orderItemsTable.getColumns().addAll(itemNameCol, itemQtyCol, itemPriceCol);
        orderItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Buttons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        Button retrieveButton = new Button("Retrieve Next Order");
        Button confirmButton = new Button("Confirm & Print Receipt");
        Button backButton = new Button("Back to Main Menu");

        // Button styles
        String buttonStyle = "-fx-background-color: #f7a400; -fx-text-fill: white;";
        String confirmStyle = "-fx-background-color: #28a745; -fx-text-fill: white;";
        String backStyle = "-fx-background-color: #6c757d; -fx-text-fill: white;";

        retrieveButton.setStyle(buttonStyle);
        confirmButton.setStyle(confirmStyle);
        backButton.setStyle(backStyle);

        // Hover effects (simplified)
        retrieveButton.setOnMouseEntered(e -> retrieveButton.setStyle("-fx-background-color: #e09500; -fx-text-fill: white;"));
        retrieveButton.setOnMouseExited(e -> retrieveButton.setStyle(buttonStyle));
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(confirmStyle));
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(backStyle));

        // Button actions
        retrieveButton.setOnAction(e -> retrieveNextOrder());
        confirmButton.setOnAction(e -> confirmOrder());
        backButton.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        buttons.getChildren().addAll(retrieveButton, confirmButton);

        // Status message
        statusLabel = new Label("No order retrieved.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #666;");

        // Add all UI elements to root
        root.getChildren().addAll(title, orderDetailsGrid, itemsLabel, orderItemsTable, buttons, statusLabel, backButton);

        return new Scene(root, 650, 700);
    }

    //Gets the next order from the queue and shows its details.
    private void retrieveNextOrder() {
        currentProcessingOrder = orderManager.retrieveNextOrder();

        if (currentProcessingOrder != null) {
            orderIdLabel.setText("Order ID: " + currentProcessingOrder.getOrderId());
            orderTimeLabel.setText("Time Placed: " + currentProcessingOrder.getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            orderPriorityLabel.setText("Priority: " + (currentProcessingOrder.isPriority() ? "YES" : "NO"));
            orderTotalLabel.setText("Total: ₱" + String.format(Locale.US, "%.2f", currentProcessingOrder.getTotalCost()));
            orderItemsTable.setItems(FXCollections.observableArrayList(currentProcessingOrder.getItems()));
            statusLabel.setText("Order #" + currentProcessingOrder.getOrderId() + " retrieved. Verify and confirm.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Orders", "No pending orders.");
            clearOrderDisplay();
        }
    }


    //Confirms the current order and sends it to the kitchen.
    private void confirmOrder() {
        if (currentProcessingOrder == null) {
            showAlert(Alert.AlertType.WARNING, "No Order", "Please retrieve an order first.");
            return;
        }

        orderManager.sendOrderToPreparation(currentProcessingOrder);
        showAlert(Alert.AlertType.INFORMATION, "Order Confirmed", "Order sent to kitchen.");
        clearOrderDisplay();
        currentProcessingOrder = null;
    }


    //Clears order info from the screen.
    private void clearOrderDisplay() {
        orderIdLabel.setText("Order ID: N/A");
        orderTimeLabel.setText("Time Placed: N/A");
        orderPriorityLabel.setText("Priority: N/A");
        orderTotalLabel.setText("Total: ₱0.00");
        orderItemsTable.setItems(FXCollections.emptyObservableList());
        statusLabel.setText("Ready for next order.");
    }


     //Shows a pop-up message.
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}