package com.school.schoolmanagement.Teachers.Model;

public class ModelQuestionPaper {

        private int id;
        private String title;
        private String className;
        private String subject;
        private String examDate;
        private String duration;

        // Constructor
        public ModelQuestionPaper(int id, String title, String className, String subject, String examDate, String duration) {
            this.id = id;
            this.title = title;
            this.className = className;
            this.subject = subject;
            this.examDate = examDate;
            this.duration = duration;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
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

}
