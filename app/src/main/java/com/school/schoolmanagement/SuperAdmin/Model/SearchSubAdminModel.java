package com.school.schoolmanagement.SuperAdmin.Model;

public class SearchSubAdminModel {
    private String id;
    private String branchName;
    private String admin;
    private String userName;
    private String password;

    // Empty constructor for Firebase or other usages
    public SearchSubAdminModel() {
    }

    // Constructor with all fields
    public SearchSubAdminModel(String id, String branchName, String admin, String userName, String password) {
        this.id = id;
        this.branchName = branchName;
        this.admin = admin;
        this.userName = userName;
        this.password = password;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
