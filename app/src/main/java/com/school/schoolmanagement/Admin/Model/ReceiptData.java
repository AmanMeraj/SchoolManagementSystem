package com.school.schoolmanagement.Admin.Model;


public class ReceiptData {

    // Student Information
    public String studentName;
    public String className;
    public String registrationId;

    // Fee Information
    public String feesMonth;
    public String totalAmount;
    public String paidAmount;
    public String remainingAmount;

    // Payment Information
    public String paymentDate;
    public String receiptNumber;
    public String paymentMode;
    public String transactionId;

    // School Information (can be added later)
    public String schoolName;
    public String schoolAddress;
    public String schoolPhone;
    public String schoolEmail;

    // Default constructor
    public ReceiptData() {
    }

    // Constructor with basic information
    public ReceiptData(String studentName, String className, String registrationId,
                       String feesMonth, String totalAmount, String paidAmount, String remainingAmount) {
        this.studentName = studentName;
        this.className = className;
        this.registrationId = registrationId;
        this.feesMonth = feesMonth;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
    }

    // Utility methods
    public boolean isDataValid() {
        return studentName != null && !studentName.isEmpty() &&
                className != null && !className.isEmpty() &&
                feesMonth != null && !feesMonth.isEmpty();
    }

    public String getFormattedReceiptTitle() {
        return "Fee Receipt - " + (studentName != null ? studentName : "Student") +
                " (" + (feesMonth != null ? feesMonth : "Month") + ")";
    }
}
