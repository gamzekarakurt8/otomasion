package pack1;

import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {
    private String[] columnNames = {"ID", "Name", "Description", "Price"};
    private Object[][] data;
    private List<CartItem> cartItems;

    public ProductTableModel() {
        cartItems = new ArrayList<>();
        refresh();
    }

    public void refresh() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");

            List<Object[]> dataList = new ArrayList<>();
            while (rs.next()) {
                Object[] row = new Object[columnNames.length];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("name");
                row[2] = rs.getString("description");
                row[3] = rs.getDouble("price");
                dataList.add(row);
            }

            data = dataList.toArray(new Object[0][]);

            rs.close();
            stmt.close();
            conn.close();

            fireTableDataChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToCart(int rowIndex, int quantity) {
        int productId = (int) getValueAt(rowIndex, 0);
        String productName = (String) getValueAt(rowIndex, 1);
        double price = (double) getValueAt(rowIndex, 3);
        double totalPrice = price * quantity;
        cartItems.add(new CartItem(productId, productName, quantity, totalPrice));
    }

    public double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @Override
    public int getRowCount() {
        return (data == null) ? 0 : data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
