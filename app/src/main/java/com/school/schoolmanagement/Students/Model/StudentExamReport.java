package com.school.schoolmanagement.Students.Model;

public class StudentExamReport {

    public void setTitle(String title) {
        this.title = title;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private String title;
    private int progress;

        public String getTitle() {
            return title;
        }

        public int getProgress() {
            return progress;
        }
}
