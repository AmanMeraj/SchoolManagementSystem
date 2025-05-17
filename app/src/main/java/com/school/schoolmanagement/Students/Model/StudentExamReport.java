package com.school.schoolmanagement.Students.Model;

public class StudentExamReport {

        private String title;
        private int progress;

        public StudentExamReport(String title, int progress) {
            this.title = title;
            this.progress = progress;
        }

        public String getTitle() {
            return title;
        }

        public int getProgress() {
            return progress;
        }
}
