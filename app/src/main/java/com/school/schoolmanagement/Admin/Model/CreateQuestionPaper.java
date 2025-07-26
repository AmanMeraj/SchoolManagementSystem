package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class CreateQuestionPaper {
    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public ArrayList<Integer> getClassIds() {
        return classIds;
    }

    public void setClassIds(ArrayList<Integer> classIds) {
        this.classIds = classIds;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getMinimumPercentage() {
        return minimumPercentage;
    }

    public void setMinimumPercentage(int minimumPercentage) {
        this.minimumPercentage = minimumPercentage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int examId;
    public ArrayList<Integer> classIds;
    public int subjectId;
    public String date;
    public String startTime;
    public String endTime;
    public int minimumPercentage;
    public String instructions;
}
