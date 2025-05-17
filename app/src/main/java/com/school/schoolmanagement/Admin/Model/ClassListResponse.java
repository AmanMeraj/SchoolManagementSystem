package com.school.schoolmanagement.Admin.Model;

import java.util.List;

public class ClassListResponse {
    private int status;
    private String message;
    private List<ClassData> data;

    public ClassListResponse() {
    }

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

    public List<ClassData> getData() {
        return data;
    }

    public void setData(List<ClassData> data) {
        this.data = data;
    }
}
