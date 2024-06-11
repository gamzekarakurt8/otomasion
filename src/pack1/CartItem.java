package pack1;

import pack1.DatabaseConnection;

class CartItem {
    private int productId;
    private String productName;
    private int quantity;
    private double totalPrice;

    public CartItem(int productId, String productName, int quantity, double totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}