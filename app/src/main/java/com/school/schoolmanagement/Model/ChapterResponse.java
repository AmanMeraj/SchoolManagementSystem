package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class ChapterResponse {
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
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public int id;
        public String chapterName;
        public String subjectName;
    }
}
