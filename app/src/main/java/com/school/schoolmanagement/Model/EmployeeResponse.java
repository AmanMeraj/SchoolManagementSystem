package com.school.schoolmanagement.Model;

import com.google.gson.annotations.SerializedName;

public class EmployeeResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("employeeId")
    private String employeeId;

    @SerializedName("status")
    private int status;

    public EmployeeResponse() {
    }

    public EmployeeResponse(boolean success, String message, String employeeId, int status) {
        this.success = success;
        this.message = message;
        this.employeeId = employeeId;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}