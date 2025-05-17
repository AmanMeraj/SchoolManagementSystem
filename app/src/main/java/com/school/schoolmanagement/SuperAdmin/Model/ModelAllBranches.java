package com.school.schoolmanagement.SuperAdmin.Model;

public class ModelAllBranches {
    private String branchId;
    private String schoolName;
    private int numberOfStudents;
    private String adminName;

    // Empty constructor for Firebase
    public ModelAllBranches() {
    }

    public ModelAllBranches(String branchId, String schoolName, int numberOfStudents, String adminName) {
        this.branchId = branchId;
        this.schoolName = schoolName;
        this.numberOfStudents = numberOfStudents;
        this.adminName = adminName;
    }

    // Getters and Setters
    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
