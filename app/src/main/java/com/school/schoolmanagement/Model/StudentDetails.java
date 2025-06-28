package com.school.schoolmanagement.Model;

import java.io.Serializable;

public class StudentDetails implements Serializable {
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

    public int getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
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

    public double getRemainingFees() {
        return remainingFees;
    }

    public void setRemainingFees(double remainingFees) {
        this.remainingFees = remainingFees;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDiseaseIfAny() {
        return diseaseIfAny;
    }

    public void setDiseaseIfAny(String diseaseIfAny) {
        this.diseaseIfAny = diseaseIfAny;
    }

    public String getBirthFormIdOrNic() {
        return birthFormIdOrNic;
    }

    public void setBirthFormIdOrNic(String birthFormIdOrNic) {
        this.birthFormIdOrNic = birthFormIdOrNic;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(String previousSchool) {
        this.previousSchool = previousSchool;
    }

    public String getPreviousIdOrBoardRollNumber() {
        return previousIdOrBoardRollNumber;
    }

    public void setPreviousIdOrBoardRollNumber(String previousIdOrBoardRollNumber) {
        this.previousIdOrBoardRollNumber = previousIdOrBoardRollNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOsc() {
        return osc;
    }

    public void setOsc(String osc) {
        this.osc = osc;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public int getTotalSibling() {
        return totalSibling;
    }

    public void setTotalSibling(int totalSibling) {
        this.totalSibling = totalSibling;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


    public int studentId;
    public String studentName;
    public int rollNumber;
    public String className;
    public String schoolName;
    public String branchName;
    public String accountStatus;
    public String username;
    public String password;
    public double remainingFees;
    public String dateOfAdmission;
    public String mobileNumber;
    public String dateOfBirth;
    public String gender;
    public String bloodGroup;
    public String diseaseIfAny;
    public String birthFormIdOrNic;
    public String cast;
    public String previousSchool;
    public String previousIdOrBoardRollNumber;
    public String note;
    public String osc;
    public String family;
    public int totalSibling;
    public String address;
    public String profilePicture;
    public String parentName;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean selected;
//    public String feesMonth;
//    public String feesDate;
//    public int totalAmount;
//    public int depositedAmount;
//    public int dueableBalance;
}
