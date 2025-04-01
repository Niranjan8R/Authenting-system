package com.mycompany.mysqlconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.sql.*;

public class dbconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/----"; // 
    private static final String USER = "----"; // 
    private static final String PASSWORD = "-----"; // 

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected Successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        connect(); // Test the connection
    }

    private static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");

   
  
    
     // Validate Admin Login (Only Checks admin Table)
    public boolean validateAdminLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true; // Admin credentials matched
            }
        } catch (SQLException e) {
            System.out.println("Admin Login Error: " + e.getMessage());
        }
        return false; // No match found
    }
    
    
       // Method to validate user login
    public boolean validateLogin(String username, String password) {
        Connection conn = connect();
        boolean isValid = false;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
        }
        return isValid;
    }
    
       public boolean validateverifyOTP(String otp) {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    boolean isValid = false;

    try {
        conn = connect(); // Get database connection
        if (conn == null) {
            System.out.println("Database connection failed.");
            return false; // Return false if the connection fails
        }

        String query = "SELECT * FROM users WHERE otp = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, otp);
        rs = pst.executeQuery();

        if (rs.next()) {
            isValid = true; // OTP exists in the database
        }
    } catch (SQLException e) {
        System.out.println("Error in validateverifyOTP: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close(); // Close connection
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
    return isValid;
}

    
    


    public String validateSubmit(String username, String favouritebook) {
    Connection conn = connect();
    if (conn == null) {
        return null; // Return null if the connection fails
    }

    String query = "SELECT password FROM users WHERE username = ? AND favouritebook = ?"; // Ensure your table has a column named `favouritebook`
    try {
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, favouritebook);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getString("password"); // Return the password if found
        }
    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
    return null; // Return null if no match is found
}

   
    
      // Method to insert a new user into the database
    public boolean insertUser(String username, String password) {
        Connection conn = connect();
        boolean success = false;
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            
            int rowsAffected = pst.executeUpdate();  // Execute the insert query
            
            if (rowsAffected > 0) {
                success = true;  // Return true if data is successfully inserted
            }
        } catch (SQLException e) {
            System.out.println("Error during user registration: " + e.getMessage());
        }
        return success;
    }

   

    public String sendOTPIfValid(String username, String phoneNumber) {
        Connection conn = connect();
        if (conn == null) {
            return null;
        }

        String query = "SELECT * FROM users WHERE username = ? AND phone = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, phoneNumber);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Generate a 6-digit OTP
                String otpCode = String.valueOf(new Random().nextInt(900000) + 100000);

                // Send OTP via Twilio
                TwilioSMS.sendOTP(phoneNumber, otpCode);

                return otpCode; // Return OTP for further validation
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null; // Return nulla if no match is found
    }
    
    
    public static boolean isUserValid(String username, String phoneNumber) {
        boolean isValid = false;
        try {
            Connection con = connect();
            if (con == null) return false;

            String query = "SELECT * FROM users WHERE username = ? AND phone = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, phoneNumber);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                isValid = true;  // User found
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }
    
   
public static boolean updatePassword(String otp, String newPassword) {
    String query = "UPDATE users SET password = ? WHERE otp = ?";
    
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, newPassword);
        stmt.setString(2, otp);

        int updated = stmt.executeUpdate();
        
        if (updated > 0) {
            System.out.println("✅ Password updated successfully for OTP: " + otp);
            
            // Optional: Clear OTP after successful password reset
            try (PreparedStatement clearOtpStmt = conn.prepareStatement("UPDATE users SET otp = NULL WHERE otp = ?")) {
                clearOtpStmt.setString(1, otp);
                clearOtpStmt.executeUpdate();
            }

            return true;
        } else {
            System.out.println("❌ Password update failed. No matching OTP found.");
            return false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}



 

  

}

