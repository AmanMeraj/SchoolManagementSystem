package com.school.schoolmanagement.Model;

import java.util.Arrays;

public class StudentFields {
    private String studentName;
    private String mail;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
    private String lastRegistrationNumber;
    private String dateOfAdmission;
    private double discountInFees;
    private String mobileNumber;
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String diseaseIfAny;
    private String birthFormIdOrNic;
    private String cast;
    private String previousSchool;
    private String previousIdOrBoardRollNumber;
    private String note;
    private String osc;
    private String family;
    private int totalSibling;
    private String address;
    private int classId;
    private Parent parent;
    private byte[] profileImageData;  // For image upload

    // Nested Parent class
    public static class Parent {
        private String fatherName;
        private String fatherEducation;
        private String fatherNationalId;
        private String fatherMobileNumber;
        private String fatherProfession;
        private int fatherIncome;
        private String parentEmail;
        private String motherName;
        private String motherEducation;
        private String motherNationalId;
        private String motherMobileNumber;
        private String motherOccupation;
        private String motherProfession;
        private int motherIncome;

        // Getters and Setters
        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getFatherEducation() {
            return fatherEducation;
        }

        public void setFatherEducation(String fatherEducation) {
            this.fatherEducation = fatherEducation;
        }

        public String getFatherNationalId() {
            return fatherNationalId;
        }

        public void setFatherNationalId(String fatherNationalId) {
            this.fatherNationalId = fatherNationalId;
        }

        public String getFatherMobileNumber() {
            return fatherMobileNumber;
        }

        public void setFatherMobileNumber(String fatherMobileNumber) {
            this.fatherMobileNumber = fatherMobileNumber;
        }

        public String getFatherProfession() {
            return fatherProfession;
        }

        public void setFatherProfession(String fatherProfession) {
            this.fatherProfession = fatherProfession;
        }

        public int getFatherIncome() {
            return fatherIncome;
        }

        public void setFatherIncome(int fatherIncome) {
            this.fatherIncome = fatherIncome;
        }

        public String getParentEmail() {
            return parentEmail;
        }

        public void setParentEmail(String parentEmail) {
            this.parentEmail = parentEmail;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public String getMotherEducation() {
            return motherEducation;
        }

        public void setMotherEducation(String motherEducation) {
            this.motherEducation = motherEducation;
        }

        public String getMotherNationalId() {
            return motherNationalId;
        }

        public void setMotherNationalId(String motherNationalId) {
            this.motherNationalId = motherNationalId;
        }

        public String getMotherMobileNumber() {
            return motherMobileNumber;
        }

        public void setMotherMobileNumber(String motherMobileNumber) {
            this.motherMobileNumber = motherMobileNumber;
        }

        public String getMotherOccupation() {
            return motherOccupation;
        }

        public void setMotherOccupation(String motherOccupation) {
            this.motherOccupation = motherOccupation;
        }

        public String getMotherProfession() {
            return motherProfession;
        }

        public void setMotherProfession(String motherProfession) {
            this.motherProfession = motherProfession;
        }

        public int getMotherIncome() {
            return motherIncome;
        }

        public void setMotherIncome(int motherIncome) {
            this.motherIncome = motherIncome;
        }

        @Override
        public String toString() {
            return "Parent{" +
                    "fatherName='" + fatherName + '\'' +
                    ", fatherEducation='" + fatherEducation + '\'' +
                    ", fatherNationalId='" + fatherNationalId + '\'' +
                    ", fatherMobileNumber='" + fatherMobileNumber + '\'' +
                    ", fatherProfession='" + fatherProfession + '\'' +
                    ", fatherIncome=" + fatherIncome +
                    ", parentEmail='" + parentEmail + '\'' +
                    ", motherName='" + motherName + '\'' +
                    ", motherEducation='" + motherEducation + '\'' +
                    ", motherNationalId='" + motherNationalId + '\'' +
                    ", motherMobileNumber='" + motherMobileNumber + '\'' +
                    ", motherOccupation='" + motherOccupation + '\'' +
                    ", motherProfession='" + motherProfession + '\'' +
                    ", motherIncome=" + motherIncome +
                    '}';
        }
    }

    // Getters and Setters for StudentFields
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public double getDiscountInFees() {
        return discountInFees;
    }

    public void setDiscountInFees(double discountInFees) {
        this.discountInFees = discountInFees;
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

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public byte[] getProfileImageData() {
        return profileImageData;
    }

    public void setProfileImageData(byte[] profileImageData) {
        this.profileImageData = profileImageData;
    }

    @Override
    public String toString() {
        return "{" +
                "studentName='" + studentName + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", lastRegistrationNumber='" + lastRegistrationNumber + '\'' +
                ", dateOfAdmission='" + dateOfAdmission + '\'' +
                ", discountInFees=" + discountInFees +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", diseaseIfAny='" + diseaseIfAny + '\'' +
                ", birthFormIdOrNic='" + birthFormIdOrNic + '\'' +
                ", cast='" + cast + '\'' +
                ", previousSchool='" + previousSchool + '\'' +
                ", previousIdOrBoardRollNumber='" + previousIdOrBoardRollNumber + '\'' +
                ", note='" + note + '\'' +
                ", osc='" + osc + '\'' +
                ", family='" + family + '\'' +
                ", totalSibling=" + totalSibling +
                ", address='" + address + '\'' +
                ", classId=" + classId +
                ", parent=" + parent +
                ", profileImageData=" + (profileImageData != null ? "[Image data]" : "null") +
                '}';
    }
}