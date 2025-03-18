package com.school.schoolmanagement.Admin.Model;

public class PromoteStudents {

        private String id;
        private String name;
        private String studentClass;
        private boolean isSelected;

        public PromoteStudents(String id, String name, String studentClass) {
            this.id = id;
            this.name = name;
            this.studentClass = studentClass;
            this.isSelected = false;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getStudentClass() {
            return studentClass;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }


}
