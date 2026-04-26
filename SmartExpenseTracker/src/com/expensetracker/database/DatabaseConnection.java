package com.expensetracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/SmartExpenseTracker";
    private static final String USER = "root";
    private static final String PASSWORD = "YourNewPassword123";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected!");
            } catch (Exception e) {
                System.out.println("Database not connected yet (waiting on MySQL setup).");
            }
        }
        return connection;
    }
}