
package com.prestonparris.paymo.models;

public class Payment {
    private String time;
    private int id1;
    private int id2;
    private double amount;
    private String message;

    public String getTimes() {
        return time;
    }

    public Payment setTime(String time) {
        this.time = time;
        return this;
    }

    public int getId1() {
        return id1;
    }

    public Payment setId1(int id1) {
        this.id1 = id1;
        return this;
    }

    public int getId2() {
        return id2;
    }

    public Payment setId2(int id2) {
        this.id2 = id2;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public Payment setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Payment setMessage(String message) {
        this.message = message;
        return this;
    }
}
