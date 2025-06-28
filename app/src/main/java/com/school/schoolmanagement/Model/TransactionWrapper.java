package com.school.schoolmanagement.Model;

public   class TransactionWrapper {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    String date;
    String type;
    double amount;
    String paymentMethod;

    public TransactionWrapper(String date, String type, double amount, String paymentMethod) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}

