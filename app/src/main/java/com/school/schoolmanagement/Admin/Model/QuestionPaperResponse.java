package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class QuestionPaperResponse {
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
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<String> getClassNames() {
            return classNames;
        }

        public void setClassNames(ArrayList<String> classNames) {
            this.classNames = classNames;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getExamDate() {
            return examDate;
        }

        public void setExamDate(String examDate) {
            this.examDate = examDate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int id;
        public String title;
        public ArrayList<String> classNames;
        public String subject;
        public String examDate;
        public String duration;

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public int getMinimumPercentage() {
            return minimumPercentage;
        }

        public void setMinimumPercentage(int minimumPercentage) {
            this.minimumPercentage = minimumPercentage;
        }

        public String instructions;
        public int minimumPercentage;
    }
}
