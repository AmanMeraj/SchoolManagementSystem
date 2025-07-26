package com.school.schoolmanagement.Admin.Model;

public class FeesRecordResponse {
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int status;
    public String message;
    public Data data;

    public class Data{
        public int getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(int registrationId) {
            this.registrationId = registrationId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getFeesMonth() {
            return feesMonth;
        }

        public void setFeesMonth(String feesMonth) {
            this.feesMonth = feesMonth;
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

        public double getDueBalance() {
            return dueBalance;
        }

        public void setDueBalance(double dueBalance) {
            this.dueBalance = dueBalance;
        }

        public int registrationId;
        public String studentName;
        public String className;
        public String feesMonth;
        public double totalAmount;
        public double depositedAmount;
        public double dueBalance;
    }
}
