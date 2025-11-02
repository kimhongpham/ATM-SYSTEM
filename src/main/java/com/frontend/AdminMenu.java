package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminMenu extends JFrame {

    private String accountNumber;
    private String authToken;
    private JLabel lblStatus;
    String status = "Not Available";
    public AdminMenu(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        initComponents();

    }

    private void initComponents() {

        setTitle("Admin Menu");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình


        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel lblTitle = new JLabel("Admin Menu", SwingConstants.CENTER);
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 16));


        JButton btnUpdateATMCash = new JButton("Update ATM Cash");
        JButton btnUpdateATMStatus = new JButton("Update ATM Status");
        JButton btnExit = new JButton("Exit");




        btnUpdateATMCash.addActionListener(e -> {

            SwingUtilities.invokeLater(() -> {
                new ATMUpdateCashUI(accountNumber, authToken).setVisible(true);
                dispose();
            });
        });

        btnUpdateATMStatus.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new ATMStatusChangeUI(accountNumber, authToken, status).setVisible(true);
                dispose();
            });
        });


        btnExit.addActionListener(e -> {

            new LoginUI().setVisible(true);
            dispose();
        });

        // Thêm nút vào buttonPanel
        buttonPanel.add(btnUpdateATMCash);
        buttonPanel.add(btnUpdateATMStatus);
        buttonPanel.add(btnExit);


        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(lblStatus, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(mainPanel);
        updateATMStatus();
    }

    private void updateATMStatus() {
        try {
            String endpoint = "http://localhost:8080/admin/atmstatus";
            String token = authToken;


            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                conn.disconnect();

                // Giả sử API trả về trạng thái dưới dạng chuỗi (VD: "ACTIVE")
                status = response.toString();
                status = status.replace("\"", "");

                // Cập nhật trạng thái vào lblStatus
                lblStatus.setText("ATM Status: " + status);
            } else {
                lblStatus.setText("Failed to fetch ATM status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Error: Unable to connect to API");
        }
    }

}