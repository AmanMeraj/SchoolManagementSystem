package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class EmployeeList implements Serializable {
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

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<Data> data;

    public class Data implements Serializable{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(int registrationId) {
            this.registrationId = registrationId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int id;
        public int registrationId;
        public String name;
        public String role;
    }
}
