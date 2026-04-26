package com.expensetracker.models;

public class Budget {
    private int budgetID;
    private double limitAmount;
    private Category category;

    public Budget(int budgetID, double limitAmount, Category category) {
        this.budgetID = budgetID;
        this.limitAmount = limitAmount;
        this.category = category;
    }

    public boolean checkLimitStatus(double currentSpent) {
        if (currentSpent >= limitAmount) {
            System.out.println("ALERT: Budget limit exceeded for " + category.getName());
            return true;
        }
        return false;
    }
}