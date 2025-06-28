package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ExamModel {
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

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<Datum> data;

    public class Datum {

        @SerializedName("examId")
        private int examId;

        @SerializedName("examinationName")
        private String examinationName;

        @SerializedName("startDate")
        private String startDate;

        @SerializedName("endDate")
        private String endDate;

        // Getters
        public int getExamId() {
            return examId;
        }

        public String getExaminationName() {
            return examinationName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        // Setters
        public void setExamId(int examId) {
            this.examId = examId;
        }

        public void setExaminationName(String examinationName) {
            this.examinationName = examinationName;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        // Helper method to check if exam data is valid
        public boolean isValidExam() {
            return examinationName != null && !examinationName.trim().isEmpty() &&
                    startDate != null && !startDate.trim().isEmpty() &&
                    endDate != null && !endDate.trim().isEmpty();
        }

        // Helper method to get display name for spinner
        public String getDisplayName() {
            if (examinationName != null && !examinationName.trim().isEmpty()) {
                return examinationName;
            }
            return "Exam " + examId; // Fallback name
        }

        @Override
        public String toString() {
            return "ExamModel{" +
                    "examId=" + examId +
                    ", examinationName='" + examinationName + '\'' +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    '}';
        }
    }
}
