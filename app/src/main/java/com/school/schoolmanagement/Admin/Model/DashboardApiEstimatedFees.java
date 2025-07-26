package com.school.schoolmanagement.Admin.Model;

public class DashboardApiEstimatedFees {
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
        public int getEstimatedAmount() {
            return estimatedAmount;
        }

        public void setEstimatedAmount(int estimatedAmount) {
            this.estimatedAmount = estimatedAmount;
        }

        public int getCollectedAmount() {
            return collectedAmount;
        }

        public void setCollectedAmount(int collectedAmount) {
            this.collectedAmount = collectedAmount;
        }

        public int getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(int remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public int estimatedAmount;
        public int collectedAmount;
        public int remainingAmount;
    }
}
