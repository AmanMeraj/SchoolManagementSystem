package com.school.schoolmanagement.Model;

import java.io.Serializable;

public class IntroItem implements Serializable {
    private final String title, description;
    private final int imageRes;

    public IntroItem(String title, String description, int imageRes) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}
