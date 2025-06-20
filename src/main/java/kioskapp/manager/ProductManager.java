package kioskapp.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import kioskapp.model.Product;


    //Handles product data in the kiosk.

public class ProductManager {
    // List of products (updates UI automatically)
    private ObservableList<Product> products;


     //Sets up an empty product list.

    public ProductManager() {
        products = FXCollections.observableArrayList();
    }


     //Adds a new product if the ID is not already used.
      //@param product The product to add.
    public void addProduct(Product product) {
        // Check for duplicate ID
        if (products.stream().noneMatch(p -> p.getId().equals(product.getId()))) {
            products.add(product);
        } else {
            showAlert(Alert.AlertType.WARNING, "Duplicate Product ID", "Product with ID " + product.getId() + " already exists.");
        }
    }


     //Finds a product by its ID.
     //@param id The product ID.
     //@return The matching product or null.

    public Product getProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }



     //Updates a product if it exists.
     //@param updatedProduct The product with new details.
     //@return true if updated, false if not found.

    public boolean updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                return true;
            }
        }
        return false;
    }


      //Removes a product by its ID.
     //@param id The ID of the product to delete.
     //@return true if removed, false otherwise.

    public boolean deleteProduct(String id) {
        return products.removeIf(p -> p.getId().equals(id));
    }


     //Returns all products (UI can watch for changes).
     //@return The list of products.

    public ObservableList<Product> getAllProducts() {
        return products;
    }


     //Shows a simple alert box.
     //@param type    The alert type.
     //@param title   The alert title.
     //@param message The alert message.

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}