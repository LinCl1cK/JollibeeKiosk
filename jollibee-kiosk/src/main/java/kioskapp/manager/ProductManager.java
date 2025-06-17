package kioskapp.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import kioskapp.model.Product;

/**
 * Manages the collection of available products in the kiosk.
 * Provides methods for adding, retrieving, updating, and deleting products.
 */
public class ProductManager {
    private ObservableList<Product> products; // Use ObservableList for automatic UI updates

    /**
     * Constructs a new ProductManager, initializing the product list.
     */
    public ProductManager() {
        products = FXCollections.observableArrayList(); // Initialize as observable
    }

    /**
     * Adds a new product to the list.
     *
     * @param product The product to add.
     */
    public void addProduct(Product product) {
        // Prevent adding duplicate product IDs
        if (products.stream().noneMatch(p -> p.getId().equals(product.getId()))) {
            products.add(product);
        } else {
            showAlert(Alert.AlertType.WARNING, "Duplicate Product ID", "Product with ID " + product.getId() + " already exists.");
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The Product object if found, null otherwise.
     */
    public Product getProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates an existing product.
     *
     * @param updatedProduct The product object with updated details.
     * @return true if the product was updated, false if not found.
     */
    public boolean updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct); // Replace the old product with the updated one
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return true if the product was deleted, false if not found.
     */
    public boolean deleteProduct(String id) {
        return products.removeIf(p -> p.getId().equals(id));
    }

    /**
     * Gets the observable list of all products.
     * This allows UI components to observe changes in the product list.
     *
     * @return An ObservableList of Product objects.
     */
    public ObservableList<Product> getAllProducts() {
        return products;
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