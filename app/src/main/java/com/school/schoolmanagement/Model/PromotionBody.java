package com.school.schoolmanagement.Model;

import java.util.ArrayList;
import java.util.List;

public class PromotionBody {
    private List<Long> studentIds; // Changed from Integer to Long

    public PromotionBody() {
        this.studentIds = new ArrayList<>();
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}