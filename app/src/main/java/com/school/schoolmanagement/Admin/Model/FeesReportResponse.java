package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class FeesReportResponse {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    String message;
    int status;
    public ArrayList<EntryInfo> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<EntryInfo> entries) {
        this.entries = entries;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public ArrayList<EntryInfo> entries;
    public Pagination pagination;
    public class EntryInfo{
        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
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
        public String studentName;
        public String dueDate;
        public double totalAmount;
        public double depositedAmount;
    }
}
