package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Employee2  implements Serializable {
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

        public class Data implements Serializable{
                public int getEmployeeId() {
                        return employeeId;
                }

                public void setEmployeeId(int employeeId) {
                        this.employeeId = employeeId;
                }

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

                public Object getRegistrationNumber() {
                        return registrationNumber;
                }

                public void setRegistrationNumber(Object registrationNumber) {
                        this.registrationNumber = registrationNumber;
                }

                public String getEmployeeRole() {
                        return employeeRole;
                }

                public void setEmployeeRole(String employeeRole) {
                        this.employeeRole = employeeRole;
                }

                public double getMonthlySalary() {
                        return monthlySalary;
                }

                public void setMonthlySalary(double monthlySalary) {
                        this.monthlySalary = monthlySalary;
                }

                public String getUsername() {
                        return username;
                }

                public void setUsername(String username) {
                        this.username = username;
                }

                public String getPassword() {
                        return password;
                }

                public void setPassword(String password) {
                        this.password = password;
                }

                public String getFatherOrHusbandName() {
                        return fatherOrHusbandName;
                }

                public void setFatherOrHusbandName(String fatherOrHusbandName) {
                        this.fatherOrHusbandName = fatherOrHusbandName;
                }

                public String getMobileNumber() {
                        return mobileNumber;
                }

                public void setMobileNumber(String mobileNumber) {
                        this.mobileNumber = mobileNumber;
                }

                public String getEmailAddress() {
                        return emailAddress;
                }

                public void setEmailAddress(String emailAddress) {
                        this.emailAddress = emailAddress;
                }

                public String getHomeAddress() {
                        return homeAddress;
                }

                public void setHomeAddress(String homeAddress) {
                        this.homeAddress = homeAddress;
                }

                public String getNationalId() {
                        return nationalId;
                }

                public void setNationalId(String nationalId) {
                        this.nationalId = nationalId;
                }

                public String getEducation() {
                        return education;
                }

                public void setEducation(String education) {
                        this.education = education;
                }

                public String getGender() {
                        return gender;
                }

                public void setGender(String gender) {
                        this.gender = gender;
                }

                public String getReligion() {
                        return religion;
                }

                public void setReligion(String religion) {
                        this.religion = religion;
                }

                public String getBloodGroup() {
                        return bloodGroup;
                }

                public void setBloodGroup(String bloodGroup) {
                        this.bloodGroup = bloodGroup;
                }

                public String getDob() {
                        return dob;
                }

                public void setDob(String dob) {
                        this.dob = dob;
                }

                public String getDateOfJoining() {
                        return dateOfJoining;
                }

                public void setDateOfJoining(String dateOfJoining) {
                        this.dateOfJoining = dateOfJoining;
                }

                public String getExperience() {
                        return experience;
                }

                public void setExperience(String experience) {
                        this.experience = experience;
                }

                public String getAccountStatus() {
                        return accountStatus;
                }

                public void setAccountStatus(String accountStatus) {
                        this.accountStatus = accountStatus;
                }

                public int employeeId;
                public String profilePicture;
                public String name;
                public Object registrationNumber;
                public String employeeRole;
                public double monthlySalary;
                public String username;
                public String password;
                public String fatherOrHusbandName;
                public String mobileNumber;
                public String emailAddress;
                public String homeAddress;
                public String nationalId;
                public String education;
                public String gender;
                public String religion;
                public String bloodGroup;
                public String dob;
                public String dateOfJoining;
                public String experience;
                public String accountStatus;
        }

}

