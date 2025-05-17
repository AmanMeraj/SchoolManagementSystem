package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClassesWithSubjectsResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ClassesWithSubjectParentModel> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ClassesWithSubjectParentModel> getData() {
        return data;
    }
}