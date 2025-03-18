package com.school.schoolmanagement.Admin.Model;

public class ClassesWithSubjectParentModel {

        private String className;
        private int totalSubjects;
        private int totalMarks;
        private int maxMarks;

        public ClassesWithSubjectParentModel(String className, int totalSubjects, int totalMarks, int maxMarks) {
            this.className = className;
            this.totalSubjects = totalSubjects;
            this.totalMarks = totalMarks;
            this.maxMarks = maxMarks;
        }

        public String getClassName() {
            return className;
        }

        public int getTotalSubjects() {
            return totalSubjects;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public int getMaxMarks() {
            return maxMarks;
        }


}
