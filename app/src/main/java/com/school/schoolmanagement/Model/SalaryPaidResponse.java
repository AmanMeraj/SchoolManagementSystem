package com.school.schoolmanagement.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class SalaryPaidResponse implements Serializable {

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

    public int status;
    public String message;
    public ArrayList<Datum> data;


    public class Datum implements Serializable{
        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public Object getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(Object registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSalaryMonth() {
            return salaryMonth;
        }

        public void setSalaryMonth(String salaryMonth) {
            this.salaryMonth = salaryMonth;
        }

        public String getDateOfReceiving() {
            return dateOfReceiving;
        }

        public void setDateOfReceiving(String dateOfReceiving) {
            this.dateOfReceiving = dateOfReceiving;
        }

        public double getBonus() {
            return bonus;
        }

        public void setBonus(double bonus) {
            this.bonus = bonus;
        }

        public double getDeduction() {
            return deduction;
        }

        public void setDeduction(double deduction) {
            this.deduction = deduction;
        }

        public double getNetPaid() {
            return netPaid;
        }

        public void setNetPaid(double netPaid) {
            this.netPaid = netPaid;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String profilePicture;
        public String employeeName;
        public int employeeId;
        public Object registrationNumber;
        public String role;
        public String salaryMonth;
        public String dateOfReceiving;
        public double bonus;
        public double deduction;
        public double netPaid;
        public double totalAmount;
        public String status;
    }

}
