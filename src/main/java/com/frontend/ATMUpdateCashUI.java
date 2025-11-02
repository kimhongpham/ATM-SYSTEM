package com.frontend;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ATMUpdateCashUI extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(ATMUpdateCashUI.class);
    private JTextField txt50000, txt100000, txt200000, txt500000;
    Map<Integer,Integer> currentCash = new HashMap<>();

    private String accountNumber;
    private String authToken;

    public ATMUpdateCashUI(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        getATMCash();
        initComponents();
    }

    private void getATMCash() {
        String endpoint = "http://localhost:8080/admin/atm";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Accept", "application/json");

            // Kiểm tra phản hồi HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Chuyển đổi JSON sang Map<Integer, Integer>
                JSONObject jsonObject = new JSONObject(response.toString());
                for (String key : jsonObject.keySet()) {
                    currentCash.put(Integer.parseInt(key), jsonObject.getInt(key));
                }


            } else {
                System.err.println("Error: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initComponents() {
        setTitle("Update ATM Cash");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo các panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Các label và text field
        JLabel lbl50000 = new JLabel("50000:");
        JLabel lbl100000 = new JLabel("100000:");
        JLabel lbl200000 = new JLabel("200000:");
        JLabel lbl500000 = new JLabel("500000:");

        txt50000 = new JTextField();
        txt50000.setText(currentCash.get(50000).toString());
        txt100000 = new JTextField();
        txt100000.setText(currentCash.get(100000).toString());
        txt200000 = new JTextField();
        txt200000.setText(currentCash.get(200000).toString());
        txt500000 = new JTextField();
        txt500000.setText(currentCash.get(500000).toString());

        // Thêm vào inputPanel
        inputPanel.add(lbl50000);
        inputPanel.add(txt50000);
        inputPanel.add(lbl100000);
        inputPanel.add(txt100000);
        inputPanel.add(lbl200000);
        inputPanel.add(txt200000);
        inputPanel.add(lbl500000);
        inputPanel.add(txt500000);

        // Các nút Confirm và Exit
        JButton btnConfirm = new JButton("Confirm");
        JButton btnExit = new JButton("Exit");

        // Thêm nút vào buttonPanel
        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnExit);

        // Thêm các panel vào mainPanel
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thêm khoảng cách xung quanh
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Thêm mainPanel vào JFrame
        add(mainPanel);

        // Xử lý sự kiện nút Exit
        btnExit.addActionListener(e -> dispose());

        // Xử lý sự kiện nút Confirm
        btnConfirm.addActionListener(e -> {
            String cash50000 = txt50000.getText();
            String cash100000 = txt100000.getText();
            String cash200000 = txt200000.getText();
            String cash500000 = txt500000.getText();
            currentCash.put(50000, Integer.parseInt(cash50000));
            currentCash.put(100000, Integer.parseInt(cash100000));
            currentCash.put(200000, Integer.parseInt(cash200000));
            currentCash.put(500000, Integer.parseInt(cash500000));

            updateATMCash();

        });
    }

    private void updateATMCash() {
        String endpoint = "http://localhost:8080/admin/atm";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // Chuyển đổi Map sang JSON
            JSONObject jsonObject = new JSONObject(currentCash);
            String jsonPayload = jsonObject.toString();

            // Gửi dữ liệu
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Kiểm tra phản hồi HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200

                JOptionPane.showMessageDialog(
                        this,
                        "Updated ATM Cash!",
                        "Success",
                        JOptionPane.ERROR_MESSAGE
                );

                new AdminMenu(accountNumber, authToken).setVisible(true);
                dispose();


            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + responseCode,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}