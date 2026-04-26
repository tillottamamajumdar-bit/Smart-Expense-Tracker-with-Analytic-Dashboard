package com.expensetracker.models;

import java.util.Date;

public class ExpenseTransaction extends Transaction {

    public ExpenseTransaction(int transactionID, double amount, Date date, String description, Category category, User user) {
        super(transactionID, amount, date, description, category, user);
    }

    @Override
    public void updateBalance() {
        System.out.println("Subtracting $" + this.amount + " from total balance.");
    }
}