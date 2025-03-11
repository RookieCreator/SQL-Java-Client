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

public class MainGUI extends JFrame {
    private JTextArea queryInput;
    private JTextArea resultOutput;
    private JButton executeButton;
 
    public MainGUI() {
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

    // Executes SQL and displays the result
    private void executeSQL(String sql) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            if (sql.trim().toLowerCase().startsWith("select")) {
                ResultSet rs = stmt.executeQuery(sql);
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
                int rowsAffected = stmt.executeUpdate(sql);
                resultOutput.setText(rowsAffected + " row(s) affected.");
            }

        } catch (SQLException ex) {
            resultOutput.setText("SQL Error: " + ex.getMessage());
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
