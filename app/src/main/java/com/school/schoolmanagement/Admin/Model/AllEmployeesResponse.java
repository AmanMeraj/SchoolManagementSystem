package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllEmployeesResponse implements Serializable {
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

    public List<AllEmployees> getData() {
        return data;
    }

    public void setData(List<AllEmployees> data) {
        this.data = data;
    }

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<AllEmployees> data;
}
