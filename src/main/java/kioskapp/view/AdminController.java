package kioskapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import kioskapp.manager.ProductManager;
import kioskapp.model.Product;
import java.util.Locale;


 //Admin interface for managing products.
public class AdminController {
    private ProductManager productManager;

    // Input fields and table
    private TextField idField;
    private TextField nameField;
    private TextField priceField;
    private TableView<Product> productsTable;


     //Constructor that takes the product manager.
    public AdminController(ProductManager productManager) {
        this.productManager = productManager;
    }


     //Builds and returns the admin UI screen.
    public Scene getAdminScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #e0f2f7;"); // Light blue background

        // Title
        Label title = new Label("Admin: Product Management");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #007bb2;");

        // --- Input Fields ---
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));
        inputGrid.setStyle("-fx-border-color: #4fc3f7; -fx-border-width: 2px; -fx-border-radius: 8px;");

        // Input fields
        idField = new TextField();
        idField.setPromptText("Product ID (e.g., C1)");
        nameField = new TextField();
        nameField.setPromptText("Product Name");
        priceField = new TextField();
        priceField.setPromptText("Price");

        // Only allow numbers and dot
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldVal);
            }
        });

        // Add fields to grid
        inputGrid.add(new Label("ID:"), 0, 0);
        inputGrid.add(idField, 1, 0);
        inputGrid.add(new Label("Name:"), 0, 1);
        inputGrid.add(nameField, 1, 1);
        inputGrid.add(new Label("Price:"), 0, 2);
        inputGrid.add(priceField, 1, 2);

        // --- Buttons ---
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Product");
        Button updateButton = new Button("Update Product");
        Button deleteButton = new Button("Delete Product");
        Button clearButton = new Button("Clear Fields");
        Button backButton = new Button("Back to Main Menu");

        // Set styles for buttons
        String buttonStyle = "-fx-background-color: #26a69a; -fx-text-fill: white;";
        String deleteButtonStyle = "-fx-background-color: #ef5350; -fx-text-fill: white;";
        String clearButtonStyle = "-fx-background-color: #ffb300; -fx-text-fill: white;";
        String backButtonStyle = "-fx-background-color: #6c757d; -fx-text-fill: white;";

        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(deleteButtonStyle);
        clearButton.setStyle(clearButtonStyle);
        backButton.setStyle(backButtonStyle);

        // Hover effects
        // (skipped re-styling code for brevity, but logic stays the same)

        // Button actions
        addButton.setOnAction(e -> addProduct());
        updateButton.setOnAction(e -> updateProduct());
        deleteButton.setOnAction(e -> deleteProduct());
        clearButton.setOnAction(e -> clearFields());
        backButton.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        buttons.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // --- Table ---
        productsTable = new TableView<>();
        productsTable.setPrefHeight(250);
        productsTable.setItems(productManager.getAllProducts());

        // Columns
        TableColumn<Product, String> productIdCol = new TableColumn<>("ID");
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> productNameCol = new TableColumn<>("Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> productPriceCol = new TableColumn<>("Price (₱)");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format(Locale.US, "%.2f", price));
            }
        });

        productsTable.getColumns().addAll(productIdCol, productNameCol, productPriceCol);
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Fill input fields when clicking on a row
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idField.setText(newSel.getId());
                nameField.setText(newSel.getName());
                priceField.setText(String.format(Locale.US, "%.2f", newSel.getPrice()));
            } else {
                clearFields();
            }
        });

        // Add everything to the screen
        root.getChildren().addAll(title, inputGrid, buttons, productsTable, backButton);

        return new Scene(root, 700, 600);
    }


     //Adds a new product using form inputs.

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


    //Updates a product based on form inputs.

    private void updateProduct() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter the product ID.");
            return;
        }
        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Name and Price are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Price", "Price cannot be negative.");
                return;
            }
            Product updated = new Product(id, name, price);
            if (productManager.updateProduct(updated)) {
                showAlert(Alert.AlertType.INFORMATION, "Updated", "Product updated successfully.");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Product ID not found.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid price.");
        }
    }


    //Deletes a product by ID.
    private void deleteProduct() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Enter product ID to delete.");
            return;
        }

        if (productManager.deleteProduct(id)) {
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Product deleted successfully.");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Not Found", "Product ID not found.");
        }
    }


    //Clears all form input fields.

    private void clearFields() {
        idField.clear();
        nameField.clear();
        priceField.clear();
        productsTable.getSelectionModel().clearSelection();
    }


    //Shows an alert box.

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}