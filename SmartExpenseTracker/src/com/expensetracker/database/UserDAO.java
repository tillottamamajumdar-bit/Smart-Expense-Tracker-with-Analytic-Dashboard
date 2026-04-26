package com.expensetracker.database;

import com.expensetracker.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    // Method to insert a new user into the database
    public boolean registerUser(User user) {
        String sql = "INSERT INTO Users (username, email, passwordHash) VALUES (?, ?, ?)";
        
        // Pull Connection OUT of the try() block so it doesn't auto-close
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail()); 
            pstmt.setString(3, user.getPasswordHash());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    // Method to verify login credentials
    public boolean loginUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE email = ? AND passwordHash = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            // ResultSet also gets its own try-with-resources block
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a matching user is found
            }
            
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
            return false;
        }
    }
}