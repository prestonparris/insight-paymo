
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

    public void setTime(String time) {
        this.time = time;
    }

    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
