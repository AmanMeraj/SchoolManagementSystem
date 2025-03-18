package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class Teachers implements Serializable {
    private String name;
    private String subject;
    private int imageResId;

    public Teachers(String name, String subject, int imageResId) {
        this.name = name;
        this.subject = subject;
        this.imageResId = imageResId;
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
