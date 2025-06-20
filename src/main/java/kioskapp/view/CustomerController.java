package kioskapp.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import kioskapp.manager.OrderManager;
import kioskapp.manager.ProductManager;
import kioskapp.model.Order;
import kioskapp.model.OrderItem;
import kioskapp.model.Product;

import java.util.Locale;

/**
 * Controller for the Customer View.
 * Allows customers to select products, specify quantities, view their current order,
 * add/remove items, and place the final order. Includes a priority option.
 */
public class CustomerController {
    private ProductManager productManager;
    private OrderManager orderManager;
    private Order currentCustomerOrder; // The order being built by the current customer
    private ObservableList<OrderItem> currentOrderItems; // Observable list for current order display

    // UI Elements
    private TableView<Product> productsTable;
    private TableView<OrderItem> orderItemsTable;
    private Label totalCostLabel;
    private TextField quantityField;
    private CheckBox priorityCheckBox; // Checkbox for priority status

    /**
     * Constructs a CustomerController.
     *
     * @param productManager The manager for products.
     * @param orderManager   The manager for orders.
     */
    public CustomerController(ProductManager productManager, OrderManager orderManager) {
        this.productManager = productManager;
        this.orderManager = orderManager;
        // Initialize a new order for the customer session
        this.currentCustomerOrder = new Order("", false); // Order ID and priority set when placed
        this.currentOrderItems = FXCollections.observableArrayList(currentCustomerOrder.getItems());
    }

    /**
     * Creates and returns the Scene for the Customer View.
     *
     * @return The Scene object for the customer interface.
     */
    public Scene getCustomerScene() {
        VBox root = new VBox(15); // Main layout container
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fffde7;"); // Light yellow background

        Label title = new Label("Place Your Order");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #e62429;"); // Jollibee red

        // --- Product Selection Table---
        productsTable = new TableView<>();
        productsTable.setPrefHeight(200);
        productsTable.setItems(productManager.getAllProducts()); // Bind to all available products

        TableColumn<Product, String> productIdCol = new TableColumn<>("ID");
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productIdCol.setPrefWidth(50);

        TableColumn<Product, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productNameCol.setPrefWidth(200);

        TableColumn<Product, Double> productPriceCol = new TableColumn<>("Price (₱)");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceCol.setPrefWidth(100);
        productPriceCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Product, Double>() {
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

        productsTable.getColumns().addAll(productIdCol, productNameCol, productPriceCol);
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Quantity and Add Button ---
        HBox addSection = new HBox(10);
        addSection.setAlignment(Pos.CENTER_LEFT);
        Label qtyLabel = new Label("Quantity:");
        qtyLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        quantityField = new TextField("1");
        quantityField.setPrefWidth(60);
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantityField.setText(oldVal); // Restrict to numbers
            }
        });

        Button addButton = new Button("Add to Order");
        addButton.setStyle("-fx-background-color: #f7a400; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;");
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: #e09500; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;"));
        addButton.setOnMouseExited(e -> addButton.setStyle("-fx-background-color: #f7a400; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;"));
        addButton.setOnAction(e -> addSelectedItemToOrder());
        addSection.getChildren().addAll(qtyLabel, quantityField, addButton);


        // --- Current Order List ---
        Label yourOrderLabel = new Label("Your Current Order:");
        yourOrderLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e62429;");

        orderItemsTable = new TableView<>();
        orderItemsTable.setPrefHeight(150);
        orderItemsTable.setItems(currentOrderItems); // Bind to current order items

        TableColumn<OrderItem, String> orderItemNameCol = new TableColumn<>("Item");
        orderItemNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
        orderItemNameCol.setPrefWidth(150);

        TableColumn<OrderItem, Integer> orderItemQtyCol = new TableColumn<>("Qty");
        orderItemQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderItemQtyCol.setPrefWidth(60);

        TableColumn<OrderItem, Double> orderItemPriceCol = new TableColumn<>("Subtotal (₱)");
        orderItemPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderItemPriceCol.setPrefWidth(100);
        orderItemPriceCol.setCellFactory(tc -> new javafx.scene.control.TableCell<OrderItem, Double>() {
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

        orderItemsTable.getColumns().addAll(orderItemNameCol, orderItemQtyCol, orderItemPriceCol);
        orderItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // --- Remove and Total Cost Section ---
        HBox orderActions = new HBox(10);
        orderActions.setAlignment(Pos.CENTER_LEFT);
        Button removeItemButton = new Button("Remove Selected Item");
        removeItemButton.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;");
        removeItemButton.setOnMouseEntered(e -> removeItemButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;"));
        removeItemButton.setOnMouseExited(e -> removeItemButton.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8 15;"));
        removeItemButton.setOnAction(e -> removeSelectedItemFromOrder());
        orderActions.getChildren().addAll(removeItemButton);

        totalCostLabel = new Label("Total: ₱0.00");
        totalCostLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #000; -fx-padding: 5px; -fx-background-color: #ffe082; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        updateTotalCostDisplay(); // Initial update

        // Listen for changes in order items to update total cost
        currentOrderItems.addListener((javafx.collections.ListChangeListener<OrderItem>) c -> updateTotalCostDisplay());


        // --- Priority Checkbox and Place Order Button ---
        priorityCheckBox = new CheckBox("Priority Customer (Elderly/PWD/Pregnant)");
        priorityCheckBox.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
        placeOrderButton.setOnMouseEntered(e -> placeOrderButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));
        placeOrderButton.setOnMouseExited(e -> placeOrderButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);"));
        placeOrderButton.setOnAction(e -> placeOrder());

        // Layout the components
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.getChildren().addAll(priorityCheckBox, placeOrderButton);


        root.getChildren().addAll(
                title,
                new Label("Available Products:"),
                productsTable,
                addSection,
                yourOrderLabel,
                orderItemsTable,
                orderActions,
                totalCostLabel,
                new HBox(10, priorityCheckBox, placeOrderButton) {
                    { setAlignment(Pos.CENTER); }
                }
        );

        return new Scene(root, 700, 750); // Adjusted scene size
    }

    /**
     * Adds the currently selected product from the products table to the customer's current order.
     */
    private void addSelectedItemToOrder() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to add.");
            return;
        }
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter a valid number for quantity.");
            return;
        }

        OrderItem newItem = new OrderItem(selectedProduct, quantity);

        // Check if item already exists in order; if so, update quantity
        boolean found = false;
        for (OrderItem item : currentOrderItems) {
            if (item.getProduct().getId().equals(newItem.getProduct().getId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                orderItemsTable.refresh(); // Refresh the table to show updated quantity
                found = true;
                break;
            }
        }
        if (!found) {
            currentOrderItems.add(newItem);
        }
        updateTotalCostDisplay();
    }

    /**
     * Removes the currently selected order item from the customer's current order.
     */
    private void removeSelectedItemFromOrder() {
        OrderItem selectedOrderItem = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selectedOrderItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Item Selected", "Please select an item from your order to remove.");
            return;
        }

        // If quantity is more than 1, reduce quantity; otherwise, remove the item
        if (selectedOrderItem.getQuantity() > 1) {
            selectedOrderItem.setQuantity(selectedOrderItem.getQuantity() - 1);
            orderItemsTable.refresh(); // Refresh the table to show updated quantity
        } else {
            currentOrderItems.remove(selectedOrderItem);
        }
        updateTotalCostDisplay();
    }

    /**
     * Updates the displayed total cost based on the items in the current order.
     */
    private void updateTotalCostDisplay() {
        double total = currentOrderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        totalCostLabel.setText("Total: ₱" + String.format(Locale.US, "%.2f", total));
    }

    /**
     * Places the current customer order into the order management system.
     * After placing, clears the current order and prepares for a new one.
     */
    private void placeOrder() {
        if (currentOrderItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Order", "Your order is empty. Please add items before placing.");
            return;
        }

        // Create a new Order object with current items and priority status
        Order finalOrder = new Order("", priorityCheckBox.isSelected()); // ID will be set by OrderManager
        finalOrder.getItems().addAll(currentOrderItems); // Add all items to the new order object

        orderManager.placeOrder(finalOrder); // Place the order through the manager
        showAlert(Alert.AlertType.INFORMATION, "Order Placed!", "Your order has been placed. Please proceed to the cashier. Your order ID will be provided by the cashier.");

        // Clear current order for the next customer
        currentOrderItems.clear();
        currentCustomerOrder = new Order("", false); // Reset for a new order
        priorityCheckBox.setSelected(false);
        quantityField.setText("1"); // Reset quantity field
        updateTotalCostDisplay();
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