package com.expensetracker.utils;

import com.expensetracker.models.Transaction;
import java.util.List;

public interface Exportable {
    void exportToCSV(List<Transaction> data);
    void exportToPDF(List<Transaction> data);
}