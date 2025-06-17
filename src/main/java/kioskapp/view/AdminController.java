package kioskapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kioskapp.manager.ProductManager;
import kioskapp.model.Product;

import java.util.Locale;

/**
 * Controller for the Admin View.
 * Provides a user interface for administrators to perform Create, Read, Update, and Delete
 * (CRUD) operations on products.
 */
public class AdminController {
    private ProductManager productManager;

    // UI Elements
    private TextField idField;
    private TextField nameField;
    private TextField priceField;
    private TableView<Product> productsTable;

    /**
     * Constructs an AdminController.
     *
     * @param productManager The manager for products.
     */
    public AdminController(ProductManager productManager) {
        this.productManager = productManager;
    }

    /**
     * Creates and returns the Scene for the Admin View.
     *
     * @return The Scene object for the admin interface.
     */
    public Scene getAdminScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #e0f2f7;"); // Light blue background

        Label title = new Label("Admin: Product Management");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #007bb2;"); // Darker blue

        // --- Input Fields ---
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));
        inputGrid.setStyle("-fx-border-color: #4fc3f7; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-padding: 10;");

        idField = new TextField();
        idField.setPromptText("Product ID (e.g., C1)");
        nameField = new TextField();
        nameField.setPromptText("Product Name");
        priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Allow only numbers and a single decimal point
            if (!newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldVal);
            }
        });

        inputGrid.add(new Label("ID:"), 0, 0);
        inputGrid.add(idField, 1, 0);
        inputGrid.add(new Label("Name:"), 0, 1);
        inputGrid.add(nameField, 1, 1);
        inputGrid.add(new Label("Price:"), 0, 2);
        inputGrid.add(priceField, 1, 2);

        // --- Action Buttons ---
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button addButton = new Button("Add Product");
        Button updateButton = new Button("Update Product");
        Button deleteButton = new Button("Delete Product");
        Button clearButton = new Button("Clear Fields");
        Button backButton = new Button("Back to Main Menu"); // Placeholder for closing window

        String buttonStyle = "-fx-background-color: #26a69a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);";
        String deleteButtonStyle = "-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);";
        String clearButtonStyle = "-fx-background-color: #ffb300; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);";
        String backButtonStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;";


        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(deleteButtonStyle);
        clearButton.setStyle(clearButtonStyle);
        backButton.setStyle(backButtonStyle);

        // Add hover effects for buttons
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);"));
        addButton.setOnMouseExited(e -> addButton.setStyle(buttonStyle));
        updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);"));
        updateButton.setOnMouseExited(e -> updateButton.setStyle(buttonStyle));
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(deleteButtonStyle));
        clearButton.setOnMouseEntered(e -> clearButton.setStyle("-fx-background-color: #fb8c00; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);"));
        clearButton.setOnMouseExited(e -> clearButton.setStyle(clearButtonStyle));
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(backButtonStyle));


        addButton.setOnAction(e -> addProduct());
        updateButton.setOnAction(e -> updateProduct());
        deleteButton.setOnAction(e -> deleteProduct());
        clearButton.setOnAction(e -> clearFields());
        backButton.setOnAction(e -> ((Stage)root.getScene().getWindow()).close()); // Close this window

        buttons.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // --- Product Table ---
        productsTable = new TableView<>();
        productsTable.setPrefHeight(250);
        productsTable.setItems(productManager.getAllProducts()); // Bind to the observable list of products

        TableColumn<Product, String> productIdCol = new TableColumn<>("ID");
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> productNameCol = new TableColumn<>("Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> productPriceCol = new TableColumn<>("Price (₱)");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
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

        // Populate fields when a product is selected in the table
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        idField.setText(newSelection.getId());
                        nameField.setText(newSelection.getName());
                        priceField.setText(String.format(Locale.US, "%.2f", newSelection.getPrice()));
                    } else {
                        clearFields();
                    }
                });

        root.getChildren().addAll(title, inputGrid, buttons, productsTable, backButton);

        return new Scene(root, 700, 600);
    }

    /**
     * Adds a new product using the data from the input fields.
     */
    private void addProduct() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all product fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Price", "Price cannot be negative.");
                return;
            }
            Product newProduct = new Product(id, name, price);
            productManager.addProduct(newProduct);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid number for price.");
        }
    }

    /**
     * Updates an existing product using the data from the input fields.
     */
    private void updateProduct() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter the ID of the product to update.");
            return;
        }
        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Name and Price fields cannot be empty for update.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Price", "Price cannot be negative.");
                return;
            }
            Product updatedProduct = new Product(id, name, price);
            if (productManager.updateProduct(updatedProduct)) {
                showAlert(Alert.AlertType.INFORMATION, "Product Updated", "Product '" + name + "' updated successfully.");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Product Not Found", "Product with ID '" + id + "' not found.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid number for price.");
        }
    }

    /**
     * Deletes a product based on the ID in the input field.
     */
    private void deleteProduct() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter the ID of the product to delete.");
            return;
        }

        if (productManager.deleteProduct(id)) {
            showAlert(Alert.AlertType.INFORMATION, "Product Deleted", "Product with ID '" + id + "' deleted successfully.");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Product Not Found", "Product with ID '" + id + "' not found.");
        }
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        idField.clear();
        nameField.clear();
        priceField.clear();
        productsTable.getSelectionModel().clearSelection(); // Deselect item in table
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