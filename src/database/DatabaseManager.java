/*
Name: Rafael Zuniga
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: DatabaseManager
*/
package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager { 
    private static final String PROPERTIES_FILE = "resources/db_config.properties"; // Path to properties file
    private static final String USER_CREDENTIALS_FILE = "resources/db_users.properties"; // ✅ Fix path
    private static String databaseName = "project3"; // Default database

    // ✅ Read database name from properties file
    static {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(PROPERTIES_FILE)) {
            props.load(input);
            databaseName = props.getProperty("database", "project3"); // Default to "project3" if not set
        } catch (IOException e) {
            System.out.println("⚠️ Warning: Could not load database name from properties. Using default.");
        }
    }

    // ✅ Allow dynamic user login from MainGUI.java (with authentication from properties file)
    public static Connection getConnection(String user, String password) throws SQLException {
        // ✅ Load user credentials
        Properties userProps = new Properties();
        try (FileInputStream fis = new FileInputStream(USER_CREDENTIALS_FILE)) {
            userProps.load(fis);
        } catch (IOException e) {
            System.out.println("⚠️ Error loading user credentials file: " + e.getMessage());
        }

        // ✅ Ensure entered user credentials match properties file
        if (!userProps.getProperty(user, "").equals(password)) {
            throw new SQLException("⚠️ Authentication failed: Invalid credentials.");
        }

        String url = "jdbc:mysql://localhost:3306/" + databaseName;
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ This method is ONLY for logging queries (connects to operationslog)
    public static Connection getLogConnection() throws SQLException {
        String logUrl = "jdbc:mysql://localhost:3306/operationslog";
        return DriverManager.getConnection(logUrl, "project3app", "project3app");
    }

    // ✅ This method is for the accountant to access operationslog
    public static Connection getAccountantConnection(String user, String password) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/operationslog"; // ✅ Connects only to operationslog
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Test connection (Optional)
    public static void main(String[] args) {
        String testUser = "root"; // Change to a client user for testing
        String testPassword = "Resumebetterwork69@"; 

        try (Connection conn = getConnection(testUser, testPassword)) {
            System.out.println("✅ Connected to MySQL successfully! Using database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
