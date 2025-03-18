package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;
import java.util.List;

public class DummyDataGenerator {

    public static List<SubjectModel> getDummySubjects() {
        List<SubjectModel> subjects = new ArrayList<>();
        subjects.add(new SubjectModel("Mathematics", 85));
        subjects.add(new SubjectModel("Science", 78));
        subjects.add(new SubjectModel("English", 92));
        subjects.add(new SubjectModel("History", 74));
        return subjects;
    }
    public static int getMaxMarks() {
        return getDummySubjects().size() * 100; // Total subjects * 100
    }
}
