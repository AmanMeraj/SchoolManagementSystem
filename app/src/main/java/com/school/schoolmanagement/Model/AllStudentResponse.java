package com.school.schoolmanagement.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class AllStudentResponse implements Serializable {

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

    public ArrayList<StudentDetails> getData() {
        return data;
    }

    public void setData(ArrayList<StudentDetails> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<StudentDetails> data;
}
