package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClassesWithSubjectParentModel {
    @SerializedName("classId")
    private int classId;

    @SerializedName("className")
    private String className;

    @SerializedName("totalSubjects")
    private int totalSubjects;

    @SerializedName("totalExaminationMarks")
    private int totalExaminationMarks;

    @SerializedName("subjects")
    private List<SubjectModel> subjects;

    public ClassesWithSubjectParentModel(String className, int totalSubjects, int totalExaminationMarks, int maxMarks) {
        this.className = className;
        this.totalSubjects = totalSubjects;
        this.totalExaminationMarks = totalExaminationMarks;
    }

    public int getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public int getTotalSubjects() {
        return totalSubjects;
    }

    public int getTotalMarks() {
        return totalExaminationMarks;
    }

    public List<SubjectModel> getSubjects() {
        return subjects;
    }
}