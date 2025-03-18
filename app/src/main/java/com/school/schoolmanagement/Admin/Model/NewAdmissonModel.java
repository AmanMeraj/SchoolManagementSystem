package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class NewAdmissonModel implements Serializable {
    private String name;
    private String subject;

    public String getRoll() {
        return roll;
    }

    private String roll;
    private int imageResId;

    public NewAdmissonModel(String name, String subject,String roll, int imageResId) {
        this.name = name;
        this.subject = subject;
        this.imageResId = imageResId;
        this.roll= roll;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public int getImageResId() {
        return imageResId;
    }
}
