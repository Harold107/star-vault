package com.example.galacticore;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Goal.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
    public abstract GoalDao goalDao();
}