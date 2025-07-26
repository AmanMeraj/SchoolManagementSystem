package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class FeesDefaulter {
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

    public class Datum{
        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStudentClass() {
            return studentClass;
        }

        public void setStudentClass(String studentClass) {
            this.studentClass = studentClass;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public double getDueAmount() {
            return dueAmount;
        }

        public void setDueAmount(double dueAmount) {
            this.dueAmount = dueAmount;
        }

        public String getDueMonth() {
            return dueMonth;
        }

        public void setDueMonth(String dueMonth) {
            this.dueMonth = dueMonth;
        }

        public String profilePicture;
        public String name;
        public String studentClass;
        public String number;
        public double dueAmount;
        public String dueMonth;
    }
}
