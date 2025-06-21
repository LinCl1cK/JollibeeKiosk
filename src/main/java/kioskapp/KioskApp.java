package kioskapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import kioskapp.manager.OrderManager;
import kioskapp.manager.ProductManager;
import kioskapp.model.Product;
import kioskapp.view.AdminController;
import kioskapp.view.CashierController;
import kioskapp.view.CustomerController;
import kioskapp.view.QueueDisplayController;

import java.util.Objects;


public class KioskApp extends Application {

    private Stage primaryStage;
    private ProductManager productManager;
    private OrderManager orderManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Jollibee Kiosk System - Main Menu");

        try {
            Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/jobilee_logo.png")));
            this.primaryStage.getIcons().add(iconImage);
        } catch (Exception e) {
            System.err.println("Error loading stage icon: " + e.getMessage());
            // It's okay to continue without an icon, but log the error.
        }

        // Initialize managers
        productManager = new ProductManager();
        orderManager = new OrderManager();

        // Add some initial products for demonstration
        productManager.addProduct(new Product("C1", "Chickenjoy 1pc Meal", 120.00));
        productManager.addProduct(new Product("C2", "Chickenjoy 2pc Meal", 200.00));
        productManager.addProduct(new Product("S1", "Spaghetti Solo", 80.00));
        productManager.addProduct(new Product("B1", "Burger Steak 1pc", 95.00));
        productManager.addProduct(new Product("F1", "Fries Large", 70.00));
        productManager.addProduct(new Product("D1", "Coke Regular", 50.00));

        // Set up the main navigation scene
        showMainScene();
    }

    private void showMainScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        // Create buttons for navigation
        Button customerButton = new Button("Customer View");
        Button cashierButton = new Button("Cashier View");
        Button adminButton = new Button("Admin View");
        Button queueDisplayButton = new Button("Queue Display");

        // Set preferred width for buttons for consistent look
        customerButton.setPrefWidth(200);
        cashierButton.setPrefWidth(200);
        adminButton.setPrefWidth(200);
        queueDisplayButton.setPrefWidth(200);

        // Apply basic styling to buttons for a better look (Jollibee colors)
        String buttonStyle = "-fx-font-size: 18px; -fx-padding: 10 20; -fx-background-color: #e62429; -fx-text-fill: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);";
        String buttonHoverStyle = "-fx-background-color: #cc1e22; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"; // Darker on hover, slight scale
        customerButton.setStyle(buttonStyle);
        cashierButton.setStyle(buttonStyle);
        adminButton.setStyle(buttonStyle);
        queueDisplayButton.setStyle(buttonStyle);

        // Add hover effects
        customerButton.setOnMouseEntered(e -> customerButton.setStyle(buttonStyle + buttonHoverStyle));
        customerButton.setOnMouseExited(e -> customerButton.setStyle(buttonStyle));
        cashierButton.setOnMouseEntered(e -> cashierButton.setStyle(buttonStyle + buttonHoverStyle));
        cashierButton.setOnMouseExited(e -> cashierButton.setStyle(buttonStyle));
        adminButton.setOnMouseEntered(e -> adminButton.setStyle(buttonStyle + buttonHoverStyle));
        adminButton.setOnMouseExited(e -> adminButton.setStyle(buttonStyle));
        queueDisplayButton.setOnMouseEntered(e -> queueDisplayButton.setStyle(buttonStyle + buttonHoverStyle));
        queueDisplayButton.setOnMouseExited(e -> queueDisplayButton.setStyle(buttonStyle));

        // Set actions for buttons to open new windows
        customerButton.setOnAction(e -> openNewWindow("Customer View", () -> new CustomerController(productManager, orderManager).getCustomerScene()));
        cashierButton.setOnAction(e -> openNewWindow("Cashier View", () -> new CashierController(orderManager).getCashierScene()));
        adminButton.setOnAction(e -> openNewWindow("Admin View", () -> new AdminController(productManager).getAdminScene()));
        queueDisplayButton.setOnAction(e -> openNewWindow("Order Queue Display", () -> new QueueDisplayController(orderManager).getQueueDisplayScene()));

        // Add buttons to the root VBox
        root.getChildren().addAll(customerButton, cashierButton, adminButton, queueDisplayButton);

        // Create the scene and set it to the primary stage
        Scene scene = new Scene(root, 600, 450);
        root.setStyle("-fx-background-color: #fff9ed;");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openNewWindow(String title, java.util.function.Supplier<javafx.scene.Scene> sceneSupplier) {
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.setScene(sceneSupplier.get());
        try {
            Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/jobilee_logo.png")));
            newStage.getIcons().add(iconImage);
        } catch (Exception e) {
            System.err.println("Error loading icon for new stage: " + e.getMessage());
        }
        newStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}