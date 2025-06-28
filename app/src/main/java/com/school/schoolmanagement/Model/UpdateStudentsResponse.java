package com.school.schoolmanagement.Model;

import java.io.Serializable;

public class UpdateStudentsResponse implements Serializable {
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

    public int status;
 public String message;

}
