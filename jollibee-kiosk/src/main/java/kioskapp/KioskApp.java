package kioskapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kioskapp.manager.OrderManager;
import kioskapp.manager.ProductManager;
import kioskapp.model.Product;
import kioskapp.view.AdminController;
import kioskapp.view.CashierController;
import kioskapp.view.CustomerController;
import kioskapp.view.QueueDisplayController;

/**
 * Main application class for the Jollibee Kiosk.
 * This class handles the setup of the primary stage and the launching of
 * Customer, Cashier, Admin, and Queue Display views in separate windows.
 */
public class KioskApp extends Application {

    private Stage primaryStage; // The primary stage for the application
    private ProductManager productManager; // Manages product data
    private OrderManager orderManager; // Manages customer orders and queues

    /**
     * The main entry point for all JavaFX applications.
     * Initializes managers and sets up the main navigation scene.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Jollibee Kiosk System - Main Menu");

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

    /**
     * Displays the main navigation scene of the application.
     * This scene provides buttons to launch Customer, Cashier, Admin, and Queue Display views
     * in separate windows.
     */
    private void showMainScene() {
        VBox root = new VBox(20); // Vertical box with 20px spacing
        root.setAlignment(Pos.CENTER); // Center align elements
        root.setPadding(new Insets(50)); // Add padding around the box

        // Create buttons for navigation
        Button customerButton = new Button("Customer View");
        Button cashierButton = new Button("Cashier View");
        Button adminButton = new Button("Admin View");
        Button queueDisplayButton = new Button("Queue Display"); // New button for queue display

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
        Scene scene = new Scene(root, 600, 450); // Adjusted size for new button
        // Set a background color for the main scene
        root.setStyle("-fx-background-color: #fff9ed;"); // Light yellow/cream background
        primaryStage.setScene(scene);
        primaryStage.show(); // Show the stage
    }

    /**
     * Helper method to open a new window (Stage).
     *
     * @param title         The title of the new window.
     * @param sceneSupplier A functional interface to provide the Scene for the new window.
     */
    private void openNewWindow(String title, java.util.function.Supplier<javafx.scene.Scene> sceneSupplier) {
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.setScene(sceneSupplier.get());
        newStage.show();
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}