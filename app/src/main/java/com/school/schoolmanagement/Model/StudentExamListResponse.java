package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class StudentExamListResponse {
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


    public class Datum{
        public int getExamId() {
            return examId;
        }

        public void setExamId(int examId) {
            this.examId = examId;
        }

        public String getExaminationName() {
            return examinationName;
        }

        public void setExaminationName(String examinationName) {
            this.examinationName = examinationName;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int examId;
        public String examinationName;
        public String startDate;
        public String endDate;
    }
}
