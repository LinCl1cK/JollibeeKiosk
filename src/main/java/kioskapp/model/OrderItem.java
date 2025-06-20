package kioskapp.model;


 //One item in a customer's order.
public class OrderItem {
    private Product product; // The product being ordered
    private int quantity;    // How many of the product


     //Creates a new order item.
     //@param product  The product.
     //@param quantity How many.
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Get product
    public Product getProduct() {
        return product;
    }

    // Get quantity
    public int getQuantity() {
        return quantity;
    }

    // Set quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


     //Gets total price (price × quantity).
     //@return Total cost for this item.

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }


     //Returns item info as a string.
     //@return Text like "Burger x2 (₱199.00)".

    @Override
    public String toString() {
        return product.getName() + " x" + quantity + " (₱" + String.format("%.2f", getTotalPrice()) + ")";
    }
}