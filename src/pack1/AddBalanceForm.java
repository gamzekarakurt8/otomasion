package pack1;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import pack1.DatabaseConnection;

class AddBalanceForm extends JFrame {
    private int userId;
    private JTextField cardField, securityCodeField, expiryDateField, amountField;
    private JLabel balanceLabel;

    public AddBalanceForm(int userId, JLabel balanceLabel) {
        this.userId = userId;
        this.balanceLabel = balanceLabel;

        setTitle("Add Balance");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Card Number:"));
        cardField = new JTextField();
        add(cardField);

        add(new JLabel("Security Code:"));
        securityCodeField = new JTextField();
        add(securityCodeField);

        add(new JLabel("Expiry Date:"));
        expiryDateField = new JTextField();
        add(expiryDateField);

        add(new JLabel("Amount:"));
        amountField = new JTextField();
        add(amountField);

        JButton addButton = new JButton("Add Balance");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBalance();
            }
        });
        add(addButton);

        setVisible(true);
    }

    private void addBalance() {
    	double amount = Double.parseDouble(amountField.getText());
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE users SET balance = balance + ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            
            double newBalance = getCurrentBalance();
            balanceLabel.setText("Your Balance: $" + String.format("%.2f", newBalance)); // İki ondalık basamaklı biçimde göster
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double getCurrentBalance() {
    	double balance = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
}