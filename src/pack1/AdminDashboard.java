package pack1;

import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pack1.DatabaseConnection;

class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 20, 20)); // 1 sütun, esnek satır sayısı, 20 piksel yatay ve dikey boşluk
        add(panel);

        JButton productButton = new JButton("Manage Products");
        productButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageProducts();
            }
        });
        panel.add(productButton);

        JButton userButton = new JButton("Manage Users");
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageUsers();
            }
        });
        panel.add(userButton);

        setVisible(true);
    }
}

class UserDashboard extends JFrame {
    private int userId;
    private JLabel balanceLabel;

    public UserDashboard(int userId) {
        this.userId = userId;

        setTitle("User Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 20, 20)); // 1 sütun, esnek satır sayısı, 20 piksel yatay ve dikey boşluk
        add(panel);

        balanceLabel = new JLabel();
        panel.add(balanceLabel);

        JButton addBalanceButton = new JButton("Add Balance");
        addBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddBalanceForm(userId, balanceLabel);
            }
        });
        panel.add(addBalanceButton);

        JButton viewProductsButton = new JButton("View Products");
        viewProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewProducts(userId);
            }
        });
        panel.add(viewProductsButton);

        setVisible(true);

        // Update balance label with current balance
        updateBalanceLabel();
    }

    public void updateBalanceLabel() {
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
}
