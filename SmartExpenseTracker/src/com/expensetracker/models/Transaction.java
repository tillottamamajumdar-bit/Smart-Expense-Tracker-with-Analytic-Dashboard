package com.expensetracker.models;

import java.util.Date;

public abstract class Transaction {
    protected int transactionID;
    protected double amount;
    protected Date date;
    protected String description;
    protected Category category;
    protected User user;

    public Transaction(int transactionID, double amount, Date date, String description, Category category, User user) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.user = user;
    }

    // This forces child classes to define how they update the balance
    public abstract void updateBalance();
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public User getUser() { return user; }
}