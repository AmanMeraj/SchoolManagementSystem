package com.school.schoolmanagement.Model;

public class TimeSlotModel {
    private String title;
    private String time;
    private boolean isLeft; // true for left, false for right

    public TimeSlotModel(String title, String time, boolean isLeft) {
        this.title = title;
        this.time = time;
        this.isLeft = isLeft;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
