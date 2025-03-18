package com.school.schoolmanagement.Admin.Model;

public class BasicList {
        private String id;
        private String sName;
        private String className;
        private String fees;
        private String phone;

        public BasicList(String id, String sName, String className, String fees, String phone) {
            this.id = id;
            this.sName = sName;
            this.className = className;
            this.fees = fees;
            this.phone = phone;
        }

        public String getId() {
            return id;
        }

        public String getsName() {
            return sName;
        }

        public String getClassName() {
            return className;
        }

        public String getFees() {
            return fees;
        }

        public String getPhone() {
            return phone;
        }
    }


