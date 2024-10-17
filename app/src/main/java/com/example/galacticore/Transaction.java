package com.example.galacticore;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "transactions")
public class Transaction implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;
    private String category;
    private double amount;
    private String note;
    private boolean isIncome;

    // Constructors
    public Transaction() {}

    public Transaction(String date, String category, double amount, String note, boolean isIncome) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.isIncome = isIncome;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}