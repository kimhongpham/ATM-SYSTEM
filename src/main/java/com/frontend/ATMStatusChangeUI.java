package com.frontend;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ATMStatusChangeUI extends JFrame {
    private static final String[] ATM_STATES = {"ACTIVE", "OUTOFSERVICE", "MAINTENANCE", "LOWCASH"};
    private JComboBox<String> cbStatus;

    String accountNumber;
    String authToken;
    String status;
    public ATMStatusChangeUI(String accountNumber, String authToken, String status) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        this.status = status;
        setupUI();
    }

    private void setupUI() {
        setTitle("Change ATM Status");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Select ATM Status", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        cbStatus = new JComboBox<>(ATM_STATES);
        int selectedIndex = 0;
        for (int i = 0; i < ATM_STATES.length; i++) {
            if (ATM_STATES[i].equals(status)) {
                selectedIndex = i;
                break;
            }
        }
        cbStatus.setSelectedIndex(selectedIndex);
        mainPanel.add(cbStatus, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnConfirm = new JButton("Confirm");
        JButton btnExit = new JButton("Exit");

        btnConfirm.addActionListener(e -> onConfirm());
        btnExit.addActionListener(e -> {
            new AdminMenu(accountNumber, authToken).setVisible(true);
            dispose();
        });

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnExit);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void onConfirm() {
        String selectedStatus = (String) cbStatus.getSelectedItem();
        boolean success = sendATMStatusToAPI(selectedStatus);

        if (success) {
            JOptionPane.showMessageDialog(this, "ATM status updated to: " + selectedStatus,
                    "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            new AdminMenu(accountNumber, authToken).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update ATM status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean sendATMStatusToAPI(String status) {
        try {
            String endpoint = "http://localhost:8080/admin/atmstatus";
            String token = authToken;

            // Prepare payload
            Map<String, String> data = new HashMap<>();
            data.put("status", status);

            // Send POST request
            return sendPostRequest(endpoint, token, data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendPostRequest(String endpoint, String token, Map<String, String> payload) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Convert Map to JSON
            JSONObject jsonObject = new JSONObject(payload);
            String jsonPayload = jsonObject.toString();

            // Send payload
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            conn.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}