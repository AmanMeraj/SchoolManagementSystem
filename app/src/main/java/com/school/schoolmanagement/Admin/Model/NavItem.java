package com.school.schoolmanagement.Admin.Model;

public class NavItem {
    private String title;
    private int iconRes;

    public NavItem(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
}
