package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class HomeworkDetails {
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
        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getHomeworkDate() {
            return homeworkDate;
        }

        public void setHomeworkDate(String homeworkDate) {
            this.homeworkDate = homeworkDate;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public ArrayList<String> getHomeworkDetails() {
            return homeworkDetails;
        }

        public void setHomeworkDetails(ArrayList<String> homeworkDetails) {
            this.homeworkDetails = homeworkDetails;
        }

        public String teacherName;
        public String className;
        public String homeworkDate;
        public String subjectName;
        public ArrayList<String> homeworkDetails;
    }

}



