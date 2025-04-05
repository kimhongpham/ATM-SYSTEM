package com.frontend;

import com.atm.model.ATM;
import com.atm.model.ATMStatus;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginUI extends JFrame {
    private JLabel l1, l2, l3, atmStatus;
    private JTextField accountNum;
    private JPasswordField passwordText;
    private JButton btnSignIn, btnClear, btnOTP;

    private Map<ATMStatus, String> statusLabel;
    private ATMStatus currentStatus;

    public LoginUI() {

        statusLabel = new HashMap<ATMStatus, String>();
        statusLabel.put(ATMStatus.valueOf("ACTIVE"),"Đang hoạt động");
        statusLabel.put(ATMStatus.valueOf("OUTOFSERVICE"),"Tạm dừng hoạt động");
        statusLabel.put(ATMStatus.valueOf("MAINTENANCE"),"Đang bảo trì");
        statusLabel.put(ATMStatus.valueOf("LOWCASH"),"Cần tiếp quỹ");

        setTitle("LOGIN ACCOUNT");
        setLocationRelativeTo(null);
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();

    }

    private void initializeComponents() {
        l1 = new JLabel("WELCOME TO ATM");
        l1.setFont(new Font("Osward", Font.BOLD, 32));
        l1.setHorizontalAlignment(SwingConstants.CENTER);
        atmStatus = new JLabel();
        atmStatus.setFont(new Font("Raleway", Font.BOLD, 25));
        atmStatus.setHorizontalAlignment(SwingConstants.CENTER);

        ATMStatus status = ATMStatus.valueOf(getATMStatus().replace("\"", ""));
        currentStatus = status ;
        atmStatus.setText(statusLabel.get(status));

        if (status == ATMStatus.ACTIVE) {
            atmStatus.setForeground(Color.GREEN);
        }else {
            atmStatus.setForeground(Color.RED);
        }


        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        accountNum = new JTextField(15);
        accountNum.setFont(new Font("Arial", Font.BOLD, 14));

        l3 = new JLabel("PIN:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));

        passwordText = new JPasswordField(15);
        passwordText.setFont(new Font("Arial", Font.BOLD, 14));

        btnSignIn = new JButton("Sign In");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 20));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Arial", Font.BOLD, 20));


//
//        btnOTP = new JButton("Withdraw by OTP");
//        btnOTP.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(100, 50, 450, 40);
        add(l1);

        l2.setBounds(100, 175, 300, 30);
        add(l2);

        accountNum.setBounds(375, 175, 230, 40);
        add(accountNum);

        l3.setBounds(100, 270, 300, 30);
        add(l3);

        passwordText.setBounds(375, 270, 230, 40);
        add(passwordText);

        btnSignIn.setBounds(175, 400, 150, 50);
        add(btnSignIn);

        btnClear.setBounds(375, 400, 150, 50);
        add(btnClear);

//        btnOTP.setBounds(200, 420, 300, 50);
//        add(btnOTP);

        atmStatus.setBounds(100, 100, 450, 30);
        add(atmStatus);


    }

    private void addActionListeners() {
        btnSignIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                handleLogin();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clearFields();
            }
        });
//        btnOTP.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//
//                new SendOTPUI().setVisible(true);
//                dispose();
//
//            }
//        });
    }

    private String getATMStatus() {
        try {
            // URL của API
            URL url = new URL("http://localhost:8080/atmstatus");

            // Mở kết nối HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Kiểm tra phản hồi HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Trả về nội dung phản hồi từ API
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to fetch ATM status.";
        }
    }


    private void handleLogin() {
        String accountNumber = accountNum.getText();
        String pin = new String(passwordText.getPassword());

        try {
            URL url = new URL("http://localhost:8080/api/transactions/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = String.format("{\"accountNumber\":\"%s\", \"pin\":\"%s\"}", accountNumber, pin);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
                br.close();
                connection.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String authToken = jsonResponse.getString("token");

                //JOptionPane.showMessageDialog(null, "Login Successful!");



                //Hiển thị UI theo role
                if (jsonResponse.getString("role").equals("ADMIN")) {
                    new AdminMenu(accountNumber,authToken).setVisible(true);
                    dispose();
                }else if (currentStatus == ATMStatus.ACTIVE) {
                    new TransactionsUI(accountNumber, authToken).setVisible(true); // Truyền token
                    dispose();
                } else JOptionPane.showMessageDialog(null, "ATM này đang không hoạt động!", "ATM OOS", JOptionPane.ERROR_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Invalid Account Number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        accountNum.setText("");
        passwordText.setText("");
    }

    private void configureFrame() {
        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600);
        setLocation(250, 0);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}