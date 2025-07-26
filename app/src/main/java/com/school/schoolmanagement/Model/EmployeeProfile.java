package com.school.schoolmanagement.Model;

public class EmployeeProfile {
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
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

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public int getBranchId() {
            return branchId;
        }

        public void setBranchId(int branchId) {
            this.branchId = branchId;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public int id;
        public String name;
        public String mobileNumber;
        public String dateOfJoining;
        public String employeeRole;
        public double monthlySalary;
        public String fatherName;
        public String nationalId;
        public String education;
        public String gender;
        public String religion;
        public String bloodGroup;
        public int experience;
        public String email;
        public String dob;
        public String homeAddress;
        public int schoolId;
        public int branchId;
        public String profilePicture;
    }
}
