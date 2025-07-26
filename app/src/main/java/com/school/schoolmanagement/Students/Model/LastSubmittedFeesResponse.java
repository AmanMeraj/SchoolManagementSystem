package com.school.schoolmanagement.Students.Model;

public class LastSubmittedFeesResponse {
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
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStudentClass() {
            return studentClass;
        }

        public void setStudentClass(String studentClass) {
            this.studentClass = studentClass;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getDepositAmount() {
            return depositAmount;
        }

        public void setDepositAmount(double depositAmount) {
            this.depositAmount = depositAmount;
        }

        public double getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(double remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int id;
        public String studentClass;
        public String name;
        public String date;
        public double depositAmount;
        public double remainingAmount;
        public double totalAmount;
    }

}
