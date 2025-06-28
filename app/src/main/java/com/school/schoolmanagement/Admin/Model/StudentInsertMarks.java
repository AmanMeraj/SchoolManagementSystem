package com.school.schoolmanagement.Admin.Model;

import java.util.HashMap;
    public class StudentInsertMarks {
        private String id;
        private String name;
        private String parentName;
        private HashMap<String, String> subjectNames; // subjectId -> subjectName (instead of marks)

        public StudentInsertMarks(String id, String name, String parentName) {
            this.id = id;
            this.name = name;
            this.parentName = parentName;
            this.subjectNames = new HashMap<>();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getParentName() {
            return parentName;
        }

        public void addSubject(String subjectId, String subjectName) {
            subjectNames.put(subjectId, subjectName);
        }

        public String getSubjectName(String subjectId) {
            return subjectNames.get(subjectId);
        }

        private HashMap<String, String> subjectMarks = new HashMap<>();

        // Method to set marks for a specific subject
        public void setSubjectMark(String subject, String marks) {
            subjectMarks.put(subject, marks);
        }

        // Method to get marks for a specific subject
        public String getSubjectMark(String subject) {
            return subjectMarks.get(subject);
        }

        // Method to get all subject marks
        public HashMap<String, String> getAllSubjects() {
            return subjectMarks;
        }

        // Method to check if student has marks for a subject
        public boolean hasMarksForSubject(String subject) {
            return subjectMarks.containsKey(subject) && !subjectMarks.get(subject).isEmpty();
        }

    }
