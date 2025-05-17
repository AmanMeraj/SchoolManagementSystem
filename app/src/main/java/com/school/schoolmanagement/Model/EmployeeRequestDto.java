package com.school.schoolmanagement.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeRequestDto {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("employeeId")
    @Expose
    private int id;

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    @SerializedName("dateOfJoining")
    @Expose
    private String dateOfJoining;

    @SerializedName("employeeRole")
    @Expose
    private String employeeRole;

    @SerializedName("monthlySalary")
    @Expose
    private double monthlySalary;

    @SerializedName("fatherOrHusbandName")
    @Expose
    private String fatherName;

    @SerializedName("nationalId")
    @Expose
    private String nationalId;

    @SerializedName("education")
    @Expose
    private String education;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("religion")
    @Expose
    private String religion;

    @SerializedName("bloodGroup")
    @Expose
    private String bloodGroup;

    @SerializedName("experience")
    @Expose
    private String experience;

    @SerializedName("emailAddress")
    @Expose
    private String email;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("homeAddress")
    @Expose
    private String homeAddress;

    // Don't include profile picture in the base object as it will be sent separately
    // in the multipart request

    // Constructor
    public EmployeeRequestDto() {
        // Default constructor
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
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

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
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

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}