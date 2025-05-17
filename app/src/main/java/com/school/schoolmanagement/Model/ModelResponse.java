package com.school.schoolmanagement.Model;

import java.io.Serializable;

public class ModelResponse implements Serializable {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public  String status;
    public  String message;
}
