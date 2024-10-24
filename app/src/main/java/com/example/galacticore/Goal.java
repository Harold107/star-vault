package com.example.galacticore;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private boolean isCompleted;
    private long completedDate;
    private int starsEarned;

    public Goal(double amount) {
        this.amount = amount;
        this.isCompleted = false;
        this.starsEarned = 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        if (completed) {
            this.completedDate = System.currentTimeMillis();
        }
    }

    public long getCompletedDate() { return completedDate; }
    public void setCompletedDate(long completedDate) { this.completedDate = completedDate; }

    public int getStarsEarned() { return starsEarned; }
    public void setStarsEarned(int starsEarned) { this.starsEarned = starsEarned; }
}