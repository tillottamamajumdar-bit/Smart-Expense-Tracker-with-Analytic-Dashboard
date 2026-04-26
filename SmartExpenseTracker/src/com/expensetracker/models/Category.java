package com.expensetracker.models;

public class Category {
    private int categoryID;
    private String name;
    private String type; 

    public Category(int categoryID, String name, String type) {
        this.categoryID = categoryID;
        this.name = name;
        this.type = type;
    }

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() { return name; }
}