package com.frontend;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;


public class TransferUI extends JFrame {
    private JLabel l1, l2, l3;
    private JButton btnexit, btnTransfer;
    private JTextField accountNumText,amountText;
    private String accountNumber;
    private String authToken;
    public TransferUI(String accountNumber,String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }
    private void initializeComponents() {
        l1 = new JLabel("Enter The Information To Transfer");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        accountNumText = new JTextField(15);
        accountNumText.setFont(new Font("Arial", Font.BOLD, 24));

        l3 = new JLabel("Amount:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));

        amountText = new JTextField(15);
        amountText.setFont(new Font("Arial", Font.BOLD, 24));

        btnexit = new JButton("Exit");
        btnexit.setFont(new Font("Arial", Font.BOLD, 24));

        btnTransfer = new JButton("Transfer");
        btnTransfer.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(50, 50, 800, 40);
        add(l1);

        l2.setBounds(100, 175, 300, 30);
        add(l2);

        accountNumText.setBounds(375, 175, 230, 40);
        add(accountNumText);

        l3.setBounds(100, 270, 300, 30);
        add(l3);

        amountText.setBounds(375, 270, 230, 40);
        add(amountText);

        btnexit.setBounds(300, 350, 150, 50);
        add(btnexit);

        btnTransfer.setBounds(500, 350, 150, 50);
        add(btnTransfer);
    }
    private void configureFrame() {
        setSize(700, 600);
        setLocation(250, 0);
        getContentPane().setBackground(Color.WHITE);
        setVisible(true);
    }
    private void addActionListeners() {
        // Exit button functionality
        btnexit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TransactionsUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        // Transfer button functionality
        btnTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiverAccountNumber = accountNumText.getText();
                String amount = amountText.getText();

                // Simple validation
                if (receiverAccountNumber.isEmpty() || amount.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double transferAmount = Double.parseDouble(amount);
                    if (transferAmount <= 0) {
                        JOptionPane.showMessageDialog(null, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Call the API (replace with actual API call)
                    boolean success = callTransferAPI(receiverAccountNumber, transferAmount);
//                    if (success) {
//                        JOptionPane.showMessageDialog(null, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                        new TransactionsUI(accountNumber,authToken).setVisible(true);
//                        dispose();
//                    }
//                    else {
//                        JOptionPane.showMessageDialog(null, "Transfer failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    private boolean callTransferAPI(String targetAccountNumber, double amount) {
        try {
            String apiUrl = "http://localhost:8080/api/transactions/transfer";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Cấu hình HTTP Request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setDoOutput(true);

            // Tạo JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("targetAccountNumber", targetAccountNumber);
            jsonPayload.put("amount", amount);

            // Gửi JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.toString().getBytes("UTF-8"));
            }

            // Kiểm tra mã phản hồi từ server
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return handleSuccessResponse(connection);
            } else {
                return handleErrorResponse(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xử lý chuyển khoản! Vui lòng thử lại sau.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Phương thức xử lý phản hồi thành công
    private boolean handleSuccessResponse(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }

            System.out.println("Success Response: " + response.toString());
            JSONObject responseBody = new JSONObject(response.toString());
            String message = responseBody.getString("message");
            String balance = responseBody.getString("data");

            // Định dạng số liệu
            double balanceValue = Double.parseDouble(balance);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String formattedBalance = df.format(balanceValue);

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(null, message + " Số dư mới: " + formattedBalance, "Success", JOptionPane.INFORMATION_MESSAGE);
            new TransactionsUI(accountNumber, authToken).setVisible(true);
            dispose();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xử lý phản hồi thành công.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Phương thức xử lý phản hồi lỗi
    private boolean handleErrorResponse(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"))) {
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponse.append(line.trim());
            }

            System.out.println("Error Response: " + errorResponse.toString());
            JSONObject errorBody = new JSONObject(errorResponse.toString());
            String errorMessage = errorBody.getString("message");

            // Hiển thị thông báo lỗi
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xử lý phản hồi lỗi.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}
