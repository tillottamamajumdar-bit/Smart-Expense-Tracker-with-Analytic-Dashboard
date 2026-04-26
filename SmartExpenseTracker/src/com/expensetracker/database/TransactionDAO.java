package com.expensetracker.database;

import com.expensetracker.models.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDAO {

    public boolean addTransaction(Transaction tx) {
        String sql = "INSERT INTO Transactions (amount, transactionDate, description, categoryID, userID, transactionType) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, tx.getAmount());
            pstmt.setDate(2, new java.sql.Date(tx.getDate().getTime()));
            pstmt.setString(3, tx.getDescription());
            pstmt.setInt(4, tx.getCategory().getCategoryID());
            pstmt.setInt(5, tx.getUser().getUserID());
            pstmt.setString(6, tx.getCategory().getType());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Transaction Error: " + e.getMessage());
            return false;
        }
    }

    public int getCategoryIDByName(String categoryName) {
        String sql = "SELECT categoryID FROM Categories WHERE name = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("categoryID");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Category ID: " + e.getMessage());
        }
        return 1; 
    }

    public double getTotalIncome() {
        String sql = "SELECT SUM(amount) AS total FROM Transactions WHERE transactionType = 'Income'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.out.println("Error fetching income: " + e.getMessage());
        }
        return 0.0;
    }

    public double getTotalExpense() {
        String sql = "SELECT SUM(amount) AS total FROM Transactions WHERE transactionType = 'Expense'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.out.println("Error fetching expense: " + e.getMessage());
        }
        return 0.0;
    }

    public double getTotalSpentByCategory(int categoryID) {
        String sql = "SELECT SUM(amount) AS total FROM Transactions WHERE categoryID = ? AND transactionType = 'Expense'";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating category total: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM Transactions WHERE transactionID = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
            return false;
        }
    }

    public javax.swing.table.DefaultTableModel getTransactionHistory() {
        String[] columnNames = {"ID", "Date", "Description", "Category", "Type", "Amount"};
        // We override getColumnClass so the sorter knows column 5 is a Number!
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };

        String sql = "SELECT t.transactionID, t.transactionDate, t.description, c.name as categoryName, t.transactionType, t.amount " +
                     "FROM Transactions t JOIN Categories c ON t.categoryID = c.categoryID " +
                     "ORDER BY t.transactionDate DESC, t.transactionID DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("transactionID"),
                    rs.getDate("transactionDate"),
                    rs.getString("description"),
                    rs.getString("categoryName"),
                    rs.getString("transactionType"),
                    rs.getDouble("amount") // Returned as a pure double
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
        return model;
    }

    public javax.swing.table.DefaultTableModel searchTransactions(String keyword) {
        String[] columnNames = {"ID", "Date", "Description", "Category", "Type", "Amount"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };

        String sql = "SELECT t.transactionID, t.transactionDate, t.description, c.name as categoryName, t.transactionType, t.amount " +
                     "FROM Transactions t JOIN Categories c ON t.categoryID = c.categoryID " +
                     "WHERE t.description LIKE ? OR c.name LIKE ? " +
                     "ORDER BY t.transactionDate DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("transactionID"),
                        rs.getDate("transactionDate"),
                        rs.getString("description"),
                        rs.getString("categoryName"),
                        rs.getString("transactionType"),
                        rs.getDouble("amount") // Returned as a pure double
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching: " + e.getMessage());
        }
        return model;
    }
}