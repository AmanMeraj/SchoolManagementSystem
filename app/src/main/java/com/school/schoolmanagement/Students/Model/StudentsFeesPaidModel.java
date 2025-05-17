package com.school.schoolmanagement.Students.Model;

public class StudentsFeesPaidModel {

        private String month;
        private String status; // "Paid" or "Awaiting"
        private double amount;
        private String submittedDate;
        private double totalAmount;
        private double totalPaid;
        private double balance;
        private boolean isExpanded;

        public StudentsFeesPaidModel(String month, String status, double amount, String submittedDate,
                            double totalAmount, double totalPaid, double balance) {
            this.month = month;
            this.status = status;
            this.amount = amount;
            this.submittedDate = submittedDate;
            this.totalAmount = totalAmount;
            this.totalPaid = totalPaid;
            this.balance = balance;
            this.isExpanded = false; // Default to collapsed
        }

        // Getters and Setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getSubmittedDate() {
            return submittedDate;
        }

        public void setSubmittedDate(String submittedDate) {
            this.submittedDate = submittedDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public double getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(double totalPaid) {
            this.totalPaid = totalPaid;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

}
