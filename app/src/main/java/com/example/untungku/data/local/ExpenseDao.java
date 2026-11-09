package com.example.untungku.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.untungku.data.entity.Expense;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insertExpense(Expense expense);

    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE tanggal = :tanggal")
    List<Expense> getExpensesByDate(String tanggal);

    @Query("SELECT SUM(nominal) FROM expenses WHERE tanggal = :tanggal")
    Double getTotalExpenseToday(String tanggal);
}