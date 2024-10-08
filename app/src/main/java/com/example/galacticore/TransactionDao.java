package com.example.galacticore;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 10")
    List<Transaction> getRecentTransactions();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isIncome = 1")
    double getTotalIncome();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isIncome = 0")
    double getTotalExpenses();

    @Query("SELECT * FROM transactions")
    List<Transaction> getAllTransactions();

    @Delete
    void delete(Transaction transaction);
}