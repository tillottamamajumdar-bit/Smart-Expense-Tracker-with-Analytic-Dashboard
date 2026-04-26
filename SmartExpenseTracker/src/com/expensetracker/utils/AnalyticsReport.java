package com.expensetracker.utils;

import com.expensetracker.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AnalyticsReport {

    public static JPanel getExpensePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String sql = "SELECT c.name, SUM(t.amount) as total " +
                     "FROM Transactions t " +
                     "JOIN Categories c ON t.categoryID = c.categoryID " +
                     "WHERE t.transactionType = 'Expense' " +
                     "GROUP BY c.name";

        // FIX: Pulled Connection OUT of the try() block so it stays open
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                dataset.setValue(rs.getString("name"), rs.getDouble("total"));
            }

        } catch (Exception e) {
            System.out.println("Error generating chart data: " + e.getMessage());
        }

        if (dataset.getItemCount() == 0) {
            dataset.setValue("No Expenses Logged", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Breakdown by Category",
                dataset,
                true,  
                true,  
                false  
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }
}