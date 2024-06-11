package pack1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurantautomation";
    private static final String USER = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = "1234"; // MySQL şifresi

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}