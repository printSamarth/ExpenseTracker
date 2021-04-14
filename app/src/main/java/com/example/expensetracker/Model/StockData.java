package com.example.expensetracker.Model;

public class StockData {
    private int units;
    private String code;
    private String date;
    private double amount;
    public StockData(){}

    public StockData(int units, String code, String date, double amount) {
        this.units = units;
        this.code = code;
        this.date = date;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
