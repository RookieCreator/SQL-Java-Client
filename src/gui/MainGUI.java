/*
Name: Rafael Zuniga
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: MainGUI
*/

package gui;

import database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class MainGUI extends JFrame {
	private static final long serialVersionUID = 1L;

    private JTextArea queryInput;
    private JTextArea resultOutput;
    private JButton executeButton;
    
    private String username;
    private String password;
 
    public MainGUI(String username, String password) {
    	this.username = username;
        this.password = password;
        // Set up the GUI window
        setTitle("SQL Client Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Query input area
        queryInput = new JTextArea(5, 40);
        JScrollPane inputScroll = new JScrollPane(queryInput);
        
        // Execute button
        executeButton = new JButton("Execute SQL");
        executeButton.addActionListener(new ExecuteQueryListener());

        // Query result display area
        resultOutput = new JTextArea(10, 40);
        resultOutput.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(resultOutput);

        // Layout panels
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Enter SQL Query:"), BorderLayout.NORTH);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(outputScroll, BorderLayout.CENTER);
    }

    // Action listener for executing SQL queries
    private class ExecuteQueryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = queryInput.getText();
            executeSQL(sql);
        }
    }

    private void executeSQL(String sql) {
        System.out.println("Trying to connect with username: " + username + " and password: " + password); // ✅ Debugging line

        try (Connection conn = DatabaseManager.getConnection(username, password);
             Statement stmt = conn.createStatement()) {

            // Restrict "theaccountant" to SELECT queries only
            if (username.equals("theaccountant") && !sql.trim().toLowerCase().startsWith("select")) {
                resultOutput.setText("❌ Permission Denied: 'theaccountant' can only run SELECT queries.");
                return;
            }

            boolean isSelect = sql.trim().toLowerCase().startsWith("select");
            int rowsAffected = 0;
            ResultSet rs = null;

            if (isSelect) {
                rs = stmt.executeQuery(sql);
                StringBuilder results = new StringBuilder();

                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        results.append(rs.getString(i)).append("\t");
                    }
                    results.append("\n");
                }
                resultOutput.setText(results.toString());
            } else {
                rowsAffected = stmt.executeUpdate(sql);

                // ✅ Ensure status message is displayed for non-query commands
                resultOutput.setText("✅ Command executed successfully! Rows affected: " + rowsAffected);
            }

            // ✅ Log the query execution in operationslog
            logQueryExecution(username, isSelect);


        } catch (SQLException ex) {
            resultOutput.setText("SQL Error: " + ex.getMessage());
        }
    }
    private void logQueryExecution(String user, boolean isSelect) {
        String logSQL;

        if (isSelect) {
            logSQL = "UPDATE operationslog.operationscount SET num_queries = num_queries + 1 WHERE login_username = ?";
        } else {
            logSQL = "UPDATE operationslog.operationscount SET num_updates = num_updates + 1 WHERE login_username = ?";
        }

        try (Connection conn = DatabaseManager.getLogConnection();
             PreparedStatement pstmt = conn.prepareStatement(logSQL)) {

            pstmt.setString(1, user);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Query logged successfully for user: " + user);
            } else {
                System.out.println("⚠️ No log entry updated. Check if login_username exists in operationscount.");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to log query for user " + user + ": " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            // Allowed users
            String[] allowedUsers = {"client1", "client2", "project3app", "theaccountant"};

            Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // ✅ DEBUG PRINT: Ensure username and password are captured
                System.out.println("Login attempt - Username: " + username + ", Password: " + password);

                // Validate if the username is in the allowed list
                boolean validUser = false;
                for (String user : allowedUsers) {
                    if (user.equals(username)) {
                        validUser = true;
                        break;
                    }
                }

                if (!validUser) {
                    JOptionPane.showMessageDialog(null, "❌ Invalid user! Please log in as client1, client2, project3app, or theaccountant.");
                    System.exit(0);
                }

                // ✅ PASS THE CREDENTIALS TO THE GUI
                MainGUI gui = new MainGUI(username, password);
                gui.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}