package com.expensetracker.models;

import java.util.Date;

public class IncomeTransaction extends Transaction {

    public IncomeTransaction(int transactionID, double amount, Date date, String description, Category category, User user) {
        super(transactionID, amount, date, description, category, user);
    }

    @Override
    public void updateBalance() {
        System.out.println("Adding $" + this.amount + " to total balance.");
    }
}