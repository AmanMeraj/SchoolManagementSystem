package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class StudentInfoReport {
    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

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

    public ArrayList<Datum> data;
    public String message;
    public int status;

    public class Datum{
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

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getLastRegistrationNumber() {
            return lastRegistrationNumber;
        }

        public void setLastRegistrationNumber(String lastRegistrationNumber) {
            this.lastRegistrationNumber = lastRegistrationNumber;
        }

        public String getDateOfAdmission() {
            return dateOfAdmission;
        }

        public void setDateOfAdmission(String dateOfAdmission) {
            this.dateOfAdmission = dateOfAdmission;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }

        public int studentId;
        public String studentName;
        public String schoolName;
        public String rollNumber;
        public String registrationNumber;
        public String lastRegistrationNumber;
        public String dateOfAdmission;
        public String mobileNumber;
        public String gender;
        public String dateOfBirth;
        public String address;
        public String classId;
        public String className;
        public Parent parent;
    }
}
