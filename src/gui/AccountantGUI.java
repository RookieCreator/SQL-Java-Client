/*
Name: Rafael Zuniga
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: AccountantGUI
*/
package gui;

import database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountantGUI extends JFrame {
    private static final long serialVersionUID = 1L; // Fix serialization warning

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea queryInput;
    private JTextArea logOutput;
    private JButton connectButton, disconnectButton, executeButton, clearSQLButton, clearResultButton, exitButton;
    private Connection conn = null; // Stores active connection

    public AccountantGUI() {
        setTitle("Specialized Accountant Application");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for connection details
        JPanel connectionPanel = new JPanel(new GridLayout(4, 2));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection Details"));

        connectionPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        connectionPanel.add(usernameField);

        connectionPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        connectionPanel.add(passwordField);

        connectButton = new JButton("Connect to Database");
        disconnectButton = new JButton("Disconnect from Database");
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        // SQL Query Input Area
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("Enter An SQL Command"));

        queryInput = new JTextArea(3, 50);
        JScrollPane inputScroll = new JScrollPane(queryInput);
        queryPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel queryButtons = new JPanel();
        clearSQLButton = new JButton("Clear SQL Command");
        executeButton = new JButton("Execute SQL Command");
        queryButtons.add(clearSQLButton);
        queryButtons.add(executeButton);
        queryPanel.add(queryButtons, BorderLayout.SOUTH);

        // Query Result Area (✅ Now Scrollable & Properly Positioned)
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("SQL Execution Result Window"));

        logOutput = new JTextArea(15, 50);
        logOutput.setEditable(false);
        logOutput.setFont(new Font("Monospaced", Font.PLAIN, 12)); // ✅ Ensures table-like formatting
        logOutput.setWrapStyleWord(true);
        logOutput.setLineWrap(true);
        JScrollPane outputScroll = new JScrollPane(logOutput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultPanel.add(outputScroll, BorderLayout.CENTER);

        clearResultButton = new JButton("Clear Result Window");
        clearResultButton.addActionListener(e -> logOutput.setText(""));
        resultPanel.add(clearResultButton, BorderLayout.SOUTH);

        // Exit Button
        exitButton = new JButton("Close Application");

        // Add Panels
        add(connectionPanel, BorderLayout.NORTH);
        add(queryPanel, BorderLayout.CENTER);
     // ✅ Fix: Use a new bottom panel to contain both result panel & exit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultPanel, BorderLayout.CENTER);
        bottomPanel.add(exitButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH); // 


        // ✅ Button Actions
        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());
        executeButton.addActionListener(e -> executeQuery());
        clearSQLButton.addActionListener(e -> queryInput.setText(""));
        clearResultButton.addActionListener(e -> logOutput.setText(""));
        exitButton.addActionListener(e -> System.exit(0));
    }



    private void connectToDatabase() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            conn = DatabaseManager.getAccountantConnection(username, password);
            logOutput.setText("✅ Connected to database successfully!");
        } catch (SQLException ex) {
            logOutput.setText("❌ Connection failed: " + ex.getMessage());
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (conn != null) {
                conn.close();
                logOutput.setText("🔌 Disconnected from database.");
                conn = null;
            }
        } catch (SQLException ex) {
            logOutput.setText("⚠️ Error disconnecting: " + ex.getMessage());
        }
    }

    private void executeQuery() {
        if (conn == null) {
            SwingUtilities.invokeLater(() -> logOutput.setText("❌ No database connection."));
            System.out.println("❌ No database connection.");
            return;
        }

        String sql = queryInput.getText().trim();
        System.out.println("📝 Executing query: " + sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // ✅ Build results string
            StringBuilder results = new StringBuilder();

            int columnCount = rs.getMetaData().getColumnCount();

            // ✅ Append column headers
            for (int i = 1; i <= columnCount; i++) {
                results.append(rs.getMetaData().getColumnName(i)).append("\t");
            }
            results.append("\n--------------------------------------------------\n");

            // ✅ Process and store results
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                for (int i = 1; i <= columnCount; i++) {
                    results.append(rs.getString(i)).append("\t");
                }
                results.append("\n");
            }

            if (!hasResults) {
                results.append("⚠️ No data found.\n");
                System.out.println("⚠️ Query executed but returned no results.");
            } else {
                System.out.println("✅ Query executed successfully. Data retrieved:\n" + results);
            }

            // ✅ Force GUI Update
            SwingUtilities.invokeLater(() -> {
                logOutput.setText(results.toString());
                logOutput.setCaretPosition(0); // Scroll to top
                logOutput.repaint(); // ✅ Ensure UI updates visually
            });

        } catch (SQLException ex) {
            SwingUtilities.invokeLater(() -> logOutput.setText("SQL Error: " + ex.getMessage()));
            System.out.println("❌ SQL Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountantGUI gui = new AccountantGUI();
            gui.setVisible(true);
        });
    }
}