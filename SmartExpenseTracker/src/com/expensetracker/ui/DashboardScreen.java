package com.expensetracker.ui;

import com.expensetracker.database.TransactionDAO;
import com.expensetracker.models.*;
import com.expensetracker.utils.AnalyticsReport;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardScreen extends JFrame {

    private JTextField amountField, descField, searchField;
    private JComboBox<String> typeBox, categoryBox, sortBox; 
    private JButton saveButton, searchButton, clearSearchButton, deleteButton;
    private JPanel chartPanel; 
    private JTable historyTable; 
    private JLabel incomeLabel, expenseLabel, balanceLabel;

    public DashboardScreen() {
        setTitle("Smart Expense Tracker - Dashboard");
        setSize(1050, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Area: Header & Summary Panel ---
        JPanel topAreaPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Welcome to your Financial Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        topAreaPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        incomeLabel = new JLabel("Total Income: $0.00", SwingConstants.CENTER);
        incomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        incomeLabel.setForeground(new Color(46, 204, 113)); 

        expenseLabel = new JLabel("Total Expenses: $0.00", SwingConstants.CENTER);
        expenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        expenseLabel.setForeground(new Color(231, 76, 60)); 

        balanceLabel = new JLabel("Net Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(52, 152, 219)); 

        summaryPanel.add(incomeLabel);
        summaryPanel.add(expenseLabel);
        summaryPanel.add(balanceLabel);
        topAreaPanel.add(summaryPanel, BorderLayout.SOUTH);
        add(topAreaPanel, BorderLayout.NORTH);

        // --- Left Panel: Transaction Form ---
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 10, 10)); 
        formPanel.setBorder(BorderFactory.createTitledBorder("Log New Transaction"));
        formPanel.setPreferredSize(new Dimension(250, 0));

        formPanel.add(new JLabel("Amount ($):"));
        amountField = new JTextField();
        formPanel.add(amountField);

        formPanel.add(new JLabel("Description:"));
        descField = new JTextField();
        formPanel.add(descField);

        formPanel.add(new JLabel("Type:"));
        typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        formPanel.add(typeBox);

        formPanel.add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{"Groceries", "Salary", "Food", "Rent", "Entertainment", "Transportation", "Utilities", "Freelance", "Healthcare", "Academics", "Dividends", "Shopping"});
        formPanel.add(categoryBox);

        saveButton = new JButton("Save Transaction");
        formPanel.add(saveButton);
        add(formPanel, BorderLayout.WEST);

        // --- Center Panel: Chart & Table ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10)); 
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        chartPanel = new JPanel(new BorderLayout()); 
        chartPanel.setBorder(BorderFactory.createTitledBorder("Expense Analytics"));
        centerPanel.add(chartPanel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Transaction History"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");
        
        sortBox = new JComboBox<>(new String[]{"Sort: Newest Date", "Sort: Highest Amount", "Sort: Lowest Amount"});
        
        controlPanel.add(new JLabel("Search: "));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(clearSearchButton);
        controlPanel.add(Box.createHorizontalStrut(20)); 
        controlPanel.add(sortBox);
        tablePanel.add(controlPanel, BorderLayout.NORTH);

        historyTable = new JTable();
        tablePanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton = new JButton("Delete Selected Row");
        deleteButton.setBackground(new Color(231, 76, 60)); 
        deleteButton.setForeground(Color.WHITE);
        actionPanel.add(deleteButton);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        centerPanel.add(tablePanel);
        add(centerPanel, BorderLayout.CENTER);

        // --- Event Listeners ---
        saveButton.addActionListener(e -> saveTransaction());
        searchButton.addActionListener(e -> applySearchAndSort());
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            applySearchAndSort(); 
        });
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        sortBox.addActionListener(e -> applySearchAndSort());

        // --- UX Polish ---
        getRootPane().setDefaultButton(saveButton); 
        searchField.setToolTipText("Search by description or category");
        deleteButton.setToolTipText("Highlight a row above to delete it");

        refreshDashboard();
    }

    private void applySearchAndSort() {
        TransactionDAO dao = new TransactionDAO();
        String keyword = searchField.getText();
        
        DefaultTableModel model = keyword.isEmpty() ? dao.getTransactionHistory() : dao.searchTransactions(keyword);
        historyTable.setModel(model);

        // Custom Renderer: Paints the $ and colors text Red/Green
        historyTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(String.format("$%.2f", (Double) value)); 
                    String type = (String) table.getValueAt(row, 4);
                    if (!isSelected) {
                        setForeground(type.equals("Expense") ? new Color(231, 76, 60) : new Color(46, 204, 113));
                    }
                }
                return c;
            }
        });

        // Apply Programmatic Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        historyTable.setRowSorter(sorter);
        
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int selectedIndex = sortBox.getSelectedIndex();
        
        if (selectedIndex == 0) {
            sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING)); // Date
        } else if (selectedIndex == 1) {
            sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING)); // Highest Amount
        } else if (selectedIndex == 2) {
            sortKeys.add(new RowSorter.SortKey(5, SortOrder.ASCENDING)); // Lowest Amount
        }
        
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void deleteSelectedTransaction() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = historyTable.convertRowIndexToModel(selectedRow);
        int transactionId = (int) historyTable.getModel().getValueAt(modelRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            TransactionDAO dao = new TransactionDAO();
            if (dao.deleteTransaction(transactionId)) {
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                refreshDashboard(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTransaction() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String desc = descField.getText();
            String type = (String) typeBox.getSelectedItem();
            String selectedCategory = (String) categoryBox.getSelectedItem();

            TransactionDAO dao = new TransactionDAO();
            int catID = dao.getCategoryIDByName(selectedCategory);

            Transaction newTx = type.equals("Income") 
                ? new IncomeTransaction(0, amount, new Date(), desc, new Category(catID, selectedCategory, type), new User(1, "Bob", "", ""))
                : new ExpenseTransaction(0, amount, new Date(), desc, new Category(catID, selectedCategory, type), new User(1, "Bob", "", ""));

            if (dao.addTransaction(newTx)) {
                amountField.setText("");
                descField.setText("");
                amountField.requestFocus(); // UX Polish: moves cursor back to Amount
                refreshDashboard(); 

                if (type.equals("Expense")) {
                    double totalSpent = dao.getTotalSpentByCategory(catID);
                    double budgetLimit = 500.00; 
                    if (totalSpent > budgetLimit) {
                        String alertMsg = String.format("BUDGET ALERT!\nYou have spent $%.2f on %s.\nThis exceeds your $%.2f limit!", totalSpent, selectedCategory, budgetLimit);
                        JOptionPane.showMessageDialog(this, alertMsg, "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshDashboard() {
        chartPanel.removeAll(); 
        chartPanel.add(AnalyticsReport.getExpensePieChart(), BorderLayout.CENTER); 
        chartPanel.revalidate(); 
        chartPanel.repaint(); 

        TransactionDAO dao = new TransactionDAO();
        double totalIncome = dao.getTotalIncome();
        double totalExpense = dao.getTotalExpense();
        incomeLabel.setText(String.format("Total Income: $%.2f", totalIncome));
        expenseLabel.setText(String.format("Total Expenses: $%.2f", totalExpense));
        balanceLabel.setText(String.format("Net Balance: $%.2f", totalIncome - totalExpense));

        applySearchAndSort(); 
    }
}