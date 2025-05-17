package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubjectModel implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("totalMarks")
    private int totalMarks;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMarks() {
        return totalMarks;
    }
}