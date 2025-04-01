package com.mycompany.mysqlconnection;
import javax.swing.*;
import java.sql.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class otpverification1 {
   
    // Twilio Credentials (Replace with actual values)
    public static final String ACCOUNT_SID = "----";
    public static final String AUTH_TOKEN = "-----";
    public static final String TWILIO_PHONE_NUMBER = "-----";

    // Database Credentials
    public static final String DB_URL = "jdbc:mysql://localhost:3306/----";
    public static final String DB_USER = "----";
    public static final String DB_PASSWORD = "------";

    public static boolean validateUser(String username, String phonenumber) {
        boolean userExists = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND phonenumber = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, phonenumber);

            ResultSet rs = stmt.executeQuery();
            userExists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public static void sendOTP(String userPhoneNumber, String otpCode) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new PhoneNumber(userPhoneNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    "Your verification code is: " + otpCode)
                .create();

            System.out.println("OTP sent successfully! Message SID: " + message.getSid());

            // Store OTP in the database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE users SET otp = ? WHERE phonenumber = ?")) {

                stmt.setString(1, otpCode);
                stmt.setString(2, userPhoneNumber);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to Verify OTP from Database
    public static boolean verifyOTP(String phoneNumber, String enteredOTP) {
        boolean isValid = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT otp FROM users WHERE phonenumber = ?")) {

            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && enteredOTP.equals(rs.getString("otp"))) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    // Method to Reset Password in Database
    public static void resetPassword(String phoneNumber, String newPassword) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET password = ?, otp = NULL WHERE phonenumber = ?")) {

            stmt.setString(1, newPassword);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new otpverification1();
    }
}
