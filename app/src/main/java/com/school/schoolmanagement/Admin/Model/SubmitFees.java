package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class SubmitFees {
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFeesMonth() {
        return feesMonth;
    }

    public void setFeesMonth(String feesMonth) {
        this.feesMonth = feesMonth;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<FeesStructure2> getFeesStructure() {
        return feesStructure;
    }

    public void setFeesStructure(ArrayList<FeesStructure2> feesStructure) {
        this.feesStructure = feesStructure;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDepositedAmount() {
        return depositedAmount;
    }

    public void setDepositedAmount(double depositedAmount) {
        this.depositedAmount = depositedAmount;
    }

    public int studentId;
    public String feesMonth;
    public String date;
    public ArrayList<FeesStructure2> feesStructure;
    public double totalAmount;
    public double depositedAmount;

    public class FeesStructure2{
        public String getFeeType() {
            return feeType;
        }

        public void setFeeType(String feeType) {
            this.feeType = feeType;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String feeType;
        public int amount;
    }
}
