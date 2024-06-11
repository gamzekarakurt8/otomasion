package pack1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import pack1.DatabaseConnection;
import pack1.CartItem;
import pack1.ProductTableModel;

public class ViewProducts extends JFrame {
    private int userId;
    private JTable productTable;
    private ProductTableModel productTableModel;
    private JLabel balanceLabel;
    private JTextField quantityField;

    public ViewProducts(int userId) {
        this.userId = userId;

        setTitle("Products");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        productTableModel = new ProductTableModel();
        productTable = new JTable(productTableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToCart();
            }
        });
        panel.add(addButton);

        JButton purchaseButton = new JButton("Purchase");
        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                purchase();
            }
        });
        panel.add(purchaseButton);

        add(panel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        balanceLabel = new JLabel("Your Balance: ");
        topPanel.add(balanceLabel, BorderLayout.WEST);

        updateBalanceLabel();

        add(topPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    private void updateBalanceLabel() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balanceLabel.setText("Your Balance: $" + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > 0) {
                productTableModel.addToCart(selectedRow, quantity);
                JOptionPane.showMessageDialog(this, "Added to cart.");
            } else {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to add to cart.");
        }
    }

    private void purchase() {
        double total = productTableModel.calculateTotal();
        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Please add products to cart.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= total) {
                    conn.setAutoCommit(false);
                    for (CartItem item : productTableModel.getCartItems()) {
                        String orderQuery = "INSERT INTO orders (user_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                        PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
                        orderStmt.setInt(1, userId);
                        orderStmt.setInt(2, item.getProductId());
                        orderStmt.setInt(3, item.getQuantity());
                        orderStmt.setDouble(4, item.getTotalPrice());
                        orderStmt.executeUpdate();
                    }

                    String updateBalanceQuery = "UPDATE users SET balance = ? WHERE id = ?";
                    PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                    updateBalanceStmt.setDouble(1, balance - total);
                    updateBalanceStmt.setInt(2, userId);
                    updateBalanceStmt.executeUpdate();

                    conn.commit();
                    productTableModel.clearCart();
                    updateBalanceLabel();
                    JOptionPane.showMessageDialog(this, "Purchase successful.\nTotal: $" + total);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}