package com.school.schoolmanagement.Students.Model;

public class StudentsProfileResponse {
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

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

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

        public String getParentEmail() {
            return parentEmail;
        }

        public void setParentEmail(String parentEmail) {
            this.parentEmail = parentEmail;
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

        public double getFatherIncome() {
            return fatherIncome;
        }

        public void setFatherIncome(double fatherIncome) {
            this.fatherIncome = fatherIncome;
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

        public double getMotherIncome() {
            return motherIncome;
        }

        public void setMotherIncome(double motherIncome) {
            this.motherIncome = motherIncome;
        }

        public int studentId;
        public String studentName;
        public int rollNumber;
        public String registrationNumber;
        public String lastRegistrationNumber;
        public String dateOfAdmission;
        public double discountInFees;
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
        public String className;
        public String fatherName;
        public String fatherEducation;
        public String parentEmail;
        public String fatherNationalId;
        public String fatherMobileNumber;
        public String fatherProfession;
        public double fatherIncome;
        public String motherName;
        public String motherEducation;
        public String motherNationalId;
        public String motherMobileNumber;
        public String motherOccupation;
        public String motherProfession;
        public double motherIncome;
    }
}
