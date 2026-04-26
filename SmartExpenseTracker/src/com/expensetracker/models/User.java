package com.expensetracker.models;

import java.sql.Timestamp;

public class User {
    private int userID;
    private String username;
    private String email;
    private String passwordHash;

    public User(int userID, String username, String email, String passwordHash) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public int getUserID() { return userID; }

}