import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class verifyotp {
    public static boolean verifyotp(String enteredOTP) {
        boolean isValid = false;

        try {
            // Connect to MySQL
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/----", "----", "-----");

            // Query to check if OTP exists in the database
            String sql = "SELECT otp FROM otp WHERE otp = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, enteredOTP);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                isValid = true;  // OTP exists
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

 public static void main(String[] args) { // Add this
     
    }}

  

