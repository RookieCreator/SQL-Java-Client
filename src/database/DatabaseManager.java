package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/project3"; // Update if needed
    private static final String USER = "root"; 
    private static final String PASSWORD = "Resumebetterwork69@"; 

    // Method to establish connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Main method to test connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connected to MySQL successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
