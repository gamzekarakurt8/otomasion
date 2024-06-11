package pack1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import pack1.DatabaseConnection;

public class ManageUsers extends JFrame {
    private JList<String> userList;

    public ManageUsers() {
        setTitle("Manage Users");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add header
        listModel.addElement("ID - Username - Password - Role - Balance");

        add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        JButton changeRoleButton = new JButton("Change Role");
        JButton deleteUserButton = new JButton("Delete User");
        JButton changeBalanceButton = new JButton("Change Balance");

        buttonPanel.add(changeRoleButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(changeBalanceButton);

        add(buttonPanel, BorderLayout.EAST);

        changeRoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUserRole();
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        changeBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUserBalance();
            }
        });

        populateUserList();
        setVisible(true);
    }

    private void populateUserList() {
        DefaultListModel<String> listModel = (DefaultListModel<String>) userList.getModel();
        listModel.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                String userId = rs.getString("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                double balance = rs.getDouble("balance");
                listModel.addElement(userId + " - " + username + " - " + password + " - " + role + " - " + balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeUserRole() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex > 0) { // Skip header
            // Get selected user's id
            String selectedUser = userList.getSelectedValue();
            String[] userInfo = selectedUser.split(" - ");
            int userId = Integer.parseInt(userInfo[0]);

            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "UPDATE users SET role = IF(role = 'user', 'admin', 'user') WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.executeUpdate();

                populateUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to change role.");
        }
    }

    private void deleteUser() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex > 0) { // Skip header
            // Get selected user's id
            String selectedUser = userList.getSelectedValue();
            String[] userInfo = selectedUser.split(" - ");
            int userId = Integer.parseInt(userInfo[0]);

            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.executeUpdate();

                populateUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void changeUserBalance() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex > 0) { // Skip header
            // Get selected user's id
            String selectedUser = userList.getSelectedValue();
            String[] userInfo = selectedUser.split(" - ");
            int userId = Integer.parseInt(userInfo[0]);

            String newBalanceStr = JOptionPane.showInputDialog(this, "Enter new balance:");
            double newBalance = Double.parseDouble(newBalanceStr);

            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "UPDATE users SET balance = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setDouble(1, newBalance);
                stmt.setInt(2, userId);
                stmt.executeUpdate();

                populateUserList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to change balance.");
        }
    }
}