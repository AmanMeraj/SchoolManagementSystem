package com.school.schoolmanagement.Admin.Model;

public class StudentPassword {

        private String id;
        private String sName;
        private String className;
        private String userId;
        private String password;

        public StudentPassword(String id, String sName, String className, String userId, String password) {
            this.id = id;
            this.sName = sName;
            this.className = className;
            this.userId = userId;
            this.password = password;
        }

        public String getId() { return id; }
        public String getSName() { return sName; }
        public String getClassName() { return className; }
        public String getUserId() { return userId; }
        public String getPassword() { return password; }
    }


