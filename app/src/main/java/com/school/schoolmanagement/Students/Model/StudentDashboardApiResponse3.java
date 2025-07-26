package com.school.schoolmanagement.Students.Model;

import java.util.ArrayList;

public class StudentDashboardApiResponse3 {
    public int status;
    public String message;
    public ArrayList<Datum> data;

    // Getters and setters for main class
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public class Datum {
        public String month;
        public int amount;
        public String status;
        public String submittedDate;
        public int totalAmount;
        public int totalPaid;
        public double balance;
        public boolean isExpanded; // Add this field

        // Constructor to initialize isExpanded to false by default
        public Datum() {
            this.isExpanded = false;
        }

        // All existing getters and setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubmittedDate() {
            return submittedDate;
        }

        public void setSubmittedDate(String submittedDate) {
            this.submittedDate = submittedDate;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(int totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(int totalPaid) {
            this.totalPaid = totalPaid;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        // Add getter and setter for isExpanded
        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }
    }
}